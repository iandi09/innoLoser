package de.innovas;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.innovas.views.DefaultView;
import de.innovas.views.LoginView;
import de.innovas.views.ManagementView;
import de.innovas.views.WeightByKwView;

@SpringUI
@Theme("inno")
public class VaadinUI extends UI {

	private static final long serialVersionUID = 1L;

	@Autowired
	private SpringViewProvider viewProvider;
	
	private CssLayout navigationBar;

	@Override
	protected void init(VaadinRequest request) {
		
		//setTheme("metro");
		
		VerticalLayout root = new VerticalLayout();
		root.setSizeFull();
		root.setMargin(true);
		root.setSpacing(true);
		setContent(root);

		navigationBar = new CssLayout();
		navigationBar.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		navigationBar.addComponent(createNavigationButton("Übersicht", DefaultView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("KW", WeightByKwView.VIEW_NAME));
		Button mngmtButton = createNavigationButton("Verwaltung", ManagementView.VIEW_NAME);
		navigationBar.addComponent(mngmtButton);
		
		navigationBar.addComponent(createLogotButton());
		navigationBar.setVisible(false);
		root.addComponent(navigationBar);

		final Panel viewContainer = new Panel();
		viewContainer.setSizeFull();
		root.addComponent(viewContainer);
		root.setExpandRatio(viewContainer, 1.0f);

		Navigator navigator = new Navigator(this, viewContainer);
		navigator.addProvider(viewProvider);
		
		navigator.addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(ViewChangeEvent event) {

            	boolean isLoggedIn = isLoggedIn();
                boolean isLoginView = event.getNewView() instanceof LoginView;

                if (!isLoggedIn && !isLoginView) {
                    getNavigator().navigateTo(LoginView.VIEW_NAME);
                    return false;

                } else if (isLoggedIn && isLoginView) {
                    return false;
                }
                return true;
            }

            @Override
            public void afterViewChange(ViewChangeEvent event) {
            	boolean isLoggedIn = isLoggedIn();
            	if (isLoggedIn) {
            		navigationBar.setVisible(true);
            	}
            	mngmtButton.setVisible(isAdmin());
            }
        });
	}

	private Button createNavigationButton(String caption, final String viewName) {
		Button button = new Button(caption);
		button.addStyleName(ValoTheme.BUTTON_SMALL);
		button.addClickListener(event -> getUI().getNavigator().navigateTo(viewName));
		return button;
	}
	
	private Button createLogotButton() {
		Button button = new Button("Logout");
		button.addStyleName(ValoTheme.BUTTON_SMALL);
		button.addClickListener(event -> {
			getUI().getSession().setAttribute("user", null);
			getUI().getNavigator().navigateTo("");
			navigationBar.setVisible(false);		
		});
		return button;
	}
	
	private boolean isLoggedIn() {
		return getSession().getAttribute("user") != null;
	}
	
	private boolean isAdmin() {
		Boolean isAdmin = (Boolean) getSession().getAttribute("admin");
		return isAdmin == null ? false : isAdmin;
	}

}