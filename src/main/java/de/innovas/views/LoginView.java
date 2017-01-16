package de.innovas.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import de.innovas.service.UserService;

@SuppressWarnings("serial")
@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends VerticalLayout implements View {

	@Autowired
	private UserService userService;

	public static final String VIEW_NAME = "login";

	private TextField user;
	private PasswordField password;
	private Button loginButton;

	@PostConstruct
	void init() {

		setSizeFull();

		user = new TextField("User:");
		user.setWidth("300px");
		user.setRequired(true);

		password = new PasswordField("Password:");
		password.setWidth("300px");
		password.setRequired(true);
		password.setValue("");
		password.setNullRepresentation("");

		loginButton = new Button("Login");

		loginButton.addClickListener(event -> {
			if (!user.isValid() || !password.isValid()) {
				return;
			}
			String username = user.getValue();
			String pwd = password.getValue();

			boolean isValid = userService.checkCredentials(username, pwd);

			if (isValid) {
				getSession().setAttribute("user", username);
				getSession().setAttribute("admin", userService.getCurrentUser().isAdmin());
				getUI().getNavigator().navigateTo(DefaultView.VIEW_NAME);
			} else {
				password.setValue(null);
				password.focus();
			}
		});
		

		VerticalLayout fields = new VerticalLayout(user, password, loginButton);
		fields.setCaption("<h2>Bitte einloggen:</h2>");
		fields.setCaptionAsHtml(true);
		fields.setSizeUndefined();
		fields.setSpacing(true);

		VerticalLayout viewLayout = new VerticalLayout(fields);
		viewLayout.setSizeFull();
		viewLayout.setComponentAlignment(fields, Alignment.MIDDLE_CENTER);
		viewLayout.setStyleName(Reindeer.LAYOUT_BLUE);
		addComponent(viewLayout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// the view is constructed in the init() method()
	}
}