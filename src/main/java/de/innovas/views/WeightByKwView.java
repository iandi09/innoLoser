package de.innovas.views;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

import de.innovas.entities.RoundInfo;
import de.innovas.entities.Weight;
import de.innovas.service.RoundInfoService;
import de.innovas.service.UserService;
import de.innovas.service.WeightEntryService;

@SuppressWarnings("serial")
@SpringView(name = WeightByKwView.VIEW_NAME)
public class WeightByKwView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "weightByKw";

	@Autowired
	private WeightEntryService weightEntryService;
	@Autowired
	private RoundInfoService roundInfoService;
	@Autowired
	private UserService userService;

	private RoundInfo roundInfo;
	private Map<Integer, RoundInfo> roundInfoMap;

	private Table weightTable;
	private ComboBox roundSelect;
	private ComboBox kwSelect;
	private ComboBox nameSelect;
	private TextField weightField;

	@PostConstruct
	void init() {

		roundInfoMap = roundInfoService.getAllAsMap();

		final HorizontalLayout horizontalLayout = new HorizontalLayout();

		roundSelect = new ComboBox("Runde");
		roundSelect.setIcon(FontAwesome.REPEAT);
		roundSelect.setNullSelectionAllowed(false);
		roundSelect.addValueChangeListener(event -> {
			refreshKwSelect();
		});
		horizontalLayout.addComponent(roundSelect);

		kwSelect = new ComboBox("KW");
		kwSelect.setIcon(FontAwesome.CALENDAR);
		kwSelect.setNullSelectionAllowed(false);
		kwSelect.addValueChangeListener(event -> {
			refreshTable();
		});
		horizontalLayout.addComponent(kwSelect);

		horizontalLayout.setSpacing(true);

		weightTable = new Table();
		weightTable.setVisible(false);

		final FormLayout form = new FormLayout();

		nameSelect = new ComboBox("Name");
		nameSelect.setInputPrompt("Bitte auswählen");
		nameSelect.setIcon(FontAwesome.USER);
		form.addComponent(nameSelect);

		weightField = new TextField("Gewicht");
		weightField.setIcon(FontAwesome.BAR_CHART);
		weightField.setConverter(BigDecimal.class);
		weightField.setNullRepresentation("");
		form.addComponent(weightField);

		Button button = new Button("Eintragen");
		button.addClickListener(event -> {
			String name = getSelectedName();
			BigDecimal weight = getNewWeightEntry();
			if (name != null && weight != null) {
				addWeightEntry();
				weightField.setValue(null);
			} else {
				Notification.show("Bitte Namen und Gewicht angeben");
			}
		});

		form.addComponent(button);

		final VerticalLayout verticalLayoutMain = new VerticalLayout();
		verticalLayoutMain.addComponent(new Label("Eintragshistorie"));
		verticalLayoutMain.addComponent(horizontalLayout);
		verticalLayoutMain.addComponent(weightTable);
		verticalLayoutMain.addComponent(form);
		verticalLayoutMain.setMargin(true);
		verticalLayoutMain.setSpacing(true);
		verticalLayoutMain.setStyleName(Reindeer.LAYOUT_BLUE);
		addComponent(verticalLayoutMain);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		
		if (roundInfoMap.isEmpty()) {
			return;
		}

		for (RoundInfo round : roundInfoMap.values()) {
			roundSelect.addItem(round);
		}

		int kw;
		if (event.getParameters() != null && !event.getParameters().isEmpty()) {
			String[] param = event.getParameters().split("/");
			int round = Integer.valueOf(param[0]);
			kw = Integer.valueOf(param[1]);
			roundInfo = roundInfoMap.get(round);
		} else {
			Calendar cal = new GregorianCalendar();
			kw = cal.get(Calendar.WEEK_OF_YEAR);
			roundInfo = roundInfoMap.get(Collections.max(roundInfoMap.keySet()));
		}
		roundSelect.setValue(roundInfo);
		kwSelect.setValue(kw);

		for (String user : roundInfo.getParticipants()) {
			nameSelect.addItem(user);
		}

		final List<Weight> weightList = weightEntryService.getWeightEntryList(roundInfo, kw);
		if (!weightList.isEmpty()) {	
			fillTable(weightList);
			weightTable.setVisible(true);
		} else {
			weightTable.setVisible(false);
		}
	}

	private RoundInfo getRound() {
		return (RoundInfo) roundSelect.getValue();
	}

	private Integer getKw() {
		return (Integer) kwSelect.getValue();
	}

	private BigDecimal getNewWeightEntry() {
		return (BigDecimal) weightField.getConvertedValue();
	}

	private String getSelectedName() {
		return (String) nameSelect.getValue();
	}

	private void refreshKwSelect() {
		roundInfo = (RoundInfo) roundSelect.getValue();
		kwSelect.removeAllItems();
		for (int i = roundInfo.getStartKw(); i <= roundInfo.getStartKw() + RoundInfo.ROUND_LENGTH; i++) {
			kwSelect.addItem(i);
		}
	}

	private void refreshTable() {
		List<Weight> weightEntryList = weightEntryService.getWeightEntryList(getRound(), getKw());
		weightTable.removeAllItems();
		fillTable(weightEntryList);
	}

	private void addWeightEntry() {
		Weight latestWeightEntry = weightEntryService.getLatestWeightEntry(getRound(), getKw());
		Map<String, BigDecimal> weightMap;
		int number;
		Weight newWeightEntry;
		if (latestWeightEntry == null) {
			newWeightEntry = new Weight();
			newWeightEntry.setRound(getRound().getNumber());
			newWeightEntry.setKw(getKw());
			weightMap = new HashMap<>();
			number = 1;
		} else {
			newWeightEntry = latestWeightEntry.clone();
			weightMap = latestWeightEntry.getWeightMap();
			number = latestWeightEntry.getNumber() + 1;
		}
		weightMap.put(getSelectedName(), getNewWeightEntry());
		String currentUser = userService.getCurrentUserName();
		newWeightEntry.setNumber(number);
		newWeightEntry.setWeightMap(weightMap);
		newWeightEntry.setUser(currentUser);
		newWeightEntry.setCreationDate(new Date());
		weightEntryService.saveWeightEntry(newWeightEntry);

		weightTable.addItem(createRow(newWeightEntry), number);
	}

	private void fillTable(List<Weight> weightList) {
		weightTable.addContainerProperty("#", Integer.class, null);
		for (String name : roundInfo.getParticipants()) {
			weightTable.addContainerProperty(name, BigDecimal.class, null);
		}
		weightTable.addContainerProperty("Eingetragen am", String.class, null);
		weightTable.addContainerProperty("Benutzer", String.class, null);

		int i = 0;
		for (Weight w : weightList) {
			Object[] row = createRow(w);
			weightTable.addItem(row, i++);
		}
		weightTable.setPageLength(10);
	}

	private Object[] createRow(Weight weight) {
		Object[] row = new Object[roundInfo.getParticipants().size() + 3];
		int i = 0;
		row[i++] = weight.getNumber();
		for (String name : roundInfo.getParticipants()) {
			row[i++] = weight.getWeightMap().get(name);
		}
		row[i++] = new SimpleDateFormat("dd.MM.yyyy - HH:mm").format(weight.getCreationDate());
		row[i++] = weight.getUser();
		return row;
	}
}