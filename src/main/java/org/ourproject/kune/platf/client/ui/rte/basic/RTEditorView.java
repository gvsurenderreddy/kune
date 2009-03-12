package org.ourproject.kune.platf.client.ui.rte.basic;

import org.ourproject.kune.platf.client.View;
import org.ourproject.kune.platf.client.actions.ActionItemCollection;
import org.ourproject.kune.platf.client.ui.rte.RichTextArea.FontSize;

import com.xpn.xwiki.wysiwyg.client.dom.Document;

public interface RTEditorView extends View {

    void addActions(ActionItemCollection<Object> actions);

    void addComment(String userName);

    void adjustSize(int height);

    boolean canBeBasic();

    boolean canBeExtended();

    void copy();

    void createLink(String url);

    void cut();

    void delete();

    void focus();

    Document getDocument();

    String getHtml();

    String getText();

    void insertHorizontalRule();

    void insertHtml(String html);

    void insertImage(String url);

    void insertOrderedList();

    void insertUnorderedList();

    boolean isAttached();

    boolean isBold();

    boolean isItalic();

    boolean isStrikethrough();

    boolean isSubscript();

    boolean isSuperscript();

    boolean isUnderlined();

    void justifyCenter();

    void justifyLeft();

    void justifyRight();

    void leftIndent();

    void paste();

    void redo();

    void removeFormat();

    void rightIndent();

    void selectAll();

    void setBackColor(String color);

    void setFontName(String name);

    void setFontSize(FontSize fontSize);

    void setForeColor(String color);

    void setHtml(String html);

    void setText(String text);

    void toggleBold();

    void toggleItalic();

    void toggleStrikethrough();

    void toggleSubscript();

    void toggleSuperscript();

    void toggleUnderline();

    void undo();

    void unlink();
}
