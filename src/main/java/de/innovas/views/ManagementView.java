package de.innovas.views;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

import de.innovas.entities.RoundInfo;
import de.innovas.service.RoundInfoService;
import de.innovas.service.UserService;
import de.innovas.util.KwInfo;

@SpringView(name = ManagementView.VIEW_NAME)
@SuppressWarnings("serial")
public class ManagementView extends VerticalLayout implements View {

	@Autowired
	private UserService userService;
	@Autowired
	private RoundInfoService roundInfoService;

	private ComboBox roundSelect;

	private ListSelect participantSelect;
	
	private Button newRoundButton;

	public static final String VIEW_NAME = "management";

	@PostConstruct
	void init() {

		final VerticalLayout roundInfoLayout = new VerticalLayout();
		roundInfoLayout.addComponent(new Label("Rundeninformation"));
		roundInfoLayout.setSpacing(true);

		roundSelect = new ComboBox("Runde");
		roundSelect.setIcon(FontAwesome.REPEAT);
		roundSelect.setNullSelectionAllowed(false);
		roundSelect.addValueChangeListener(event -> {
			updateParticipants();
		});

		participantSelect = new ListSelect("Teilnehmer");
		participantSelect.setWidth("317");
		participantSelect.setNullSelectionAllowed(false);
		participantSelect.setIcon(FontAwesome.USERS);

		HorizontalLayout newParticipant = new HorizontalLayout();
		TextField name = new TextField("Name");
		name.setNullRepresentation("");
		Button addParticipantButton = new Button("Hinzufügen");
		addParticipantButton.addClickListener(event -> {
			RoundInfo selectedRound = (RoundInfo) roundSelect.getValue();
			roundInfoService.saveNewParticipant(selectedRound, name.getValue());
			name.setValue(null);
			updateParticipants();
		});

		newParticipant.addComponent(name);
		newParticipant.addComponent(addParticipantButton);
		newParticipant.setSpacing(true);

		newRoundButton = new Button("Neue Runde starten");
		newRoundButton.addClickListener(event -> {
			roundInfoService.startNewRound();
			getUI().getNavigator().navigateTo(ManagementView.VIEW_NAME);
		});

		roundInfoLayout.addComponent(roundSelect);
		roundInfoLayout.setMargin(new MarginInfo(false, false, true, false));
		roundInfoLayout.addComponent(participantSelect);
		roundInfoLayout.addComponent(newParticipant);
		roundInfoLayout.addComponent(newRoundButton);

		final FormLayout form = new FormLayout();

		TextField userName = new TextField("Benutzername");
		userName.setIcon(FontAwesome.USER);
		userName.setNullRepresentation("");
		form.addComponent(userName);

		PasswordField password = new PasswordField("Passwort");
		password.setIcon(FontAwesome.LOCK);
		password.setNullRepresentation("");
		form.addComponent(password);

		Button button = new Button("Hinzufügen");
		button.addClickListener(event -> {
			userService.registerUser(userName.getValue(), password.getValue(), false);
			Notification.show(String.format("Benutzer '%s' hinzugefügt", userName.getValue()));
			userName.setValue(null);
			password.setValue(null);
		});
		form.addComponent(button);

		VerticalLayout verticalLayoutMain = new VerticalLayout();
		verticalLayoutMain.addComponent(roundInfoLayout);
		verticalLayoutMain.addComponent(new Label("Neuen Benutzer anlegen"));
		verticalLayoutMain.addComponent(form);

		verticalLayoutMain.setSpacing(true);
		verticalLayoutMain.setMargin(true);

		addComponent(verticalLayoutMain);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		List<RoundInfo> roundInfos = roundInfoService.getAll();
		for (RoundInfo round : roundInfos) {
			roundSelect.addItem(round);
			roundSelect.setValue(round);
		}
		RoundInfo roundInfo = roundInfoService.getLatestRoundInfo();
		if (roundInfo != null && KwInfo.thisWeek().before(roundInfoService.getEndKw(roundInfo))) {
			newRoundButton.setEnabled(false);
		}
	}

	private void updateParticipants() {
		RoundInfo round = (RoundInfo) roundSelect.getValue();
		List<String> participants = round.getParticipants();
		participantSelect.removeAllItems();
		for (String name : participants) {
			participantSelect.addItem(name);
		}
	}
}