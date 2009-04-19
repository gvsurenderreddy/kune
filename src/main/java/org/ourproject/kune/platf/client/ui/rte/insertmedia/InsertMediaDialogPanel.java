package org.ourproject.kune.platf.client.ui.rte.insertmedia;

import org.ourproject.kune.platf.client.i18n.I18nTranslationService;
import org.ourproject.kune.platf.client.services.Images;
import org.ourproject.kune.platf.client.ui.dialogs.tabbed.AbstractTabbedDialogPanel;
import org.ourproject.kune.platf.client.ui.rte.insertimg.InsertMediaGroup;

import com.calclab.suco.client.events.Listener0;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;

public class InsertMediaDialogPanel extends AbstractTabbedDialogPanel implements InsertMediaDialogView {

    private static final String INSERT_MEDIA_DIALOG = "k-imdp-dialog";
    private static final String INSERT_MEDIA_DIALOG_ERROR_ID = "k-imdp-dialog-err";
    private final InsertMediaGroup insertMediaGroup;

    public InsertMediaDialogPanel(final InsertMediaDialogPresenter presenter, final I18nTranslationService i18n,
            final Images images, final InsertMediaGroup insertMediaGroup) {
        super(INSERT_MEDIA_DIALOG, i18n.t("Insert Media (audio/video)"), 390, HEIGHT + 100, 390, HEIGHT + 100, true,
                images, INSERT_MEDIA_DIALOG_ERROR_ID);
        super.setIconCls("k-film-icon");
        this.insertMediaGroup = insertMediaGroup;

        super.addHideListener(new Listener0() {
            public void onEvent() {
                insertMediaGroup.resetAll();
            }
        });

        Button insert = new Button(i18n.t("Insert"));
        insert.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(final Button button, final EventObject e) {
                presenter.onInsert();
            }
        });

        Button cancel = new Button(i18n.t("Cancel"));
        cancel.addListener(new ButtonListenerAdapter() {
            @Override
            public void onClick(final Button button, final EventObject e) {
                presenter.onCancel();
            }
        });
        addButton(insert);
        addButton(cancel);
    }

    @Override
    public void createAndShow() {
        insertMediaGroup.createAll();
        super.createAndShow();
    }
}
