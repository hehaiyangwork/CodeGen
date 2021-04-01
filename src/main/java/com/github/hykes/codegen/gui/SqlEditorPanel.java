package com.github.hykes.codegen.gui;

import com.github.hykes.codegen.gui.cmt.JBalloon;
import com.github.hykes.codegen.gui.cmt.MyDialogWrapper;
import com.github.hykes.codegen.model.IdeaContext;
import com.github.hykes.codegen.model.Table;
import com.github.hykes.codegen.parser.DefaultParser;
import com.github.hykes.codegen.parser.Parser;
import com.github.hykes.codegen.provider.FileProviderFactory;
import com.github.hykes.codegen.utils.MyNotifier;
import com.github.hykes.codegen.utils.StringUtils;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * @author hehaiyangwork@gmail.com
 * @date 2017/12/19
 */
public class SqlEditorPanel implements ActionOperator {

    private static final Logger LOGGER = Logger.getInstance(SqlEditorPanel.class);

    private IdeaContext ideaContext;
    private JPanel rootPanel;
    private Editor sqlTextArea;
    private JPanel sqlPanel;
    private JScrollPane sqlScrollPane;

    public SqlEditorPanel(IdeaContext ideaContext) {
        this.ideaContext = ideaContext;
        $$$setupUI$$$();
    }

    private void disable() {
        $$$getRootComponent$$$().getRootPane().getParent().setEnabled(false);
        $$$getRootComponent$$$().getRootPane().getParent().setVisible(false);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        rootPanel = new JPanel();
        sqlPanel = new JPanel();
        sqlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        sqlPanel.setPreferredSize(JBUI.size(600, 360));
        // 设置 sql text editor
        Document document = EditorFactory.getInstance().createDocument("");
        sqlTextArea = EditorFactory.getInstance().createEditor(document, ideaContext.getProject(), FileProviderFactory.getFileType("SQL"), false);

        sqlScrollPane = new JBScrollPane();
        sqlScrollPane.setViewportView(sqlTextArea.getComponent());
    }

    public JComponent getRootComponent() {
        return rootPanel;
    }

    /**
     * 执行OK的动作
     */
    @Override
    public void ok() {
        try {
            String sqls = sqlTextArea.getDocument().getText();
            Parser parser = new DefaultParser();
            List<Table> tables = parser.parseSQLs(sqls);
            if (tables == null || tables.isEmpty()) {
                MyNotifier.notifyError(ideaContext.getProject(), "please check sql format !");
                return;
            }

            ColumnEditorFrame frame = new ColumnEditorFrame();
            frame.newColumnEditorBySql(ideaContext, tables);
            MyDialogWrapper frameWrapper = new MyDialogWrapper(ideaContext.getProject(), frame.getRootPane());
            frameWrapper.setActionOperator(frame);
            frameWrapper.setTitle("CodeGen-SQL");
            frameWrapper.setSize(800, 550);
            frameWrapper.setResizable(false);
            frameWrapper.show();

            disable();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }

        // this.rootPanel.registerKeyboardAction(e -> disable(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    @Override
    public void cancel() { }

    @Override
    public boolean valid() {
        // 1. check empty
        String sqls = sqlTextArea.getDocument().getText();
        if (StringUtils.isBlank(sqls)) {
            JBalloon.buildSimple("Input sqls can not be empty!")
                    .show(RelativePoint.getSouthOf(this.sqlScrollPane));
            return false;
        }
        // 2. check parse
        Parser parser = new DefaultParser();
        List<Table> tables = parser.parseSQLs(sqls);
        if (tables == null || tables.isEmpty()) {
            JBalloon.buildSimple("Can not parse sqls, please check sql format!")
                    .show(RelativePoint.getSouthOf(this.sqlScrollPane));
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("CodeGen-SQL");
        frame.setContentPane(new SqlEditorPanel(null).$$$getRootComponent$$$());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        rootPanel.setLayout(new BorderLayout(0, 0));
        sqlPanel.setLayout(new BorderLayout(0, 0));
        rootPanel.add(sqlPanel, BorderLayout.CENTER);
        sqlPanel.add(sqlScrollPane, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return rootPanel;
    }
}
