package de.innovas.views;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

import de.innovas.entities.RoundInfo;
import de.innovas.entities.Weight;
import de.innovas.service.RoundInfoService;
import de.innovas.service.WeightEntryService;

@SuppressWarnings("serial")
@SpringView(name = DefaultView.VIEW_NAME)
public class DefaultView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "";

	@Autowired
	private WeightEntryService weightEntryService;
	@Autowired
	private RoundInfoService roundInfoService;

	private ComboBox roundSelect;
	private Map<Integer, RoundInfo> roundInfoMap;

	private RoundInfo roundInfo;
	private Table weightTable;

	@PostConstruct
	void init() {

		roundSelect = new ComboBox("Runde");
		roundSelect.setIcon(FontAwesome.REPEAT);
		roundSelect.setNullSelectionAllowed(false);

		roundInfoMap = new HashMap<>();

		roundInfoMap = roundInfoService.getAllAsMap();

		if (!roundInfoMap.isEmpty()) {
			roundInfo = roundInfoMap.get(java.util.Collections.max(roundInfoMap.keySet()));
			roundSelect.addValueChangeListener(event -> {
				roundInfo = (RoundInfo) roundSelect.getValue();
				fillTable();
			});
		}

		weightTable = new Table();
		weightTable.setVisible(false);

		weightTable.addItemClickListener(event -> {
			Integer kw = (Integer) event.getItem().getItemProperty("KW").getValue();
			getUI().getNavigator().navigateTo(WeightByKwView.VIEW_NAME + "/" + roundInfo.getNumber() + "/" + kw);
		});

		weightTable.setPageLength(weightTable.size());

		final VerticalLayout verticalLayoutMain = new VerticalLayout();
		verticalLayoutMain.addComponent(new Label("Übersicht"));
		verticalLayoutMain.addComponent(roundSelect);
		verticalLayoutMain.addComponent(weightTable);
		verticalLayoutMain.setMargin(true);
		verticalLayoutMain.setSpacing(true);
		addComponent(verticalLayoutMain);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		for (RoundInfo round : roundInfoMap.values()) {
			roundSelect.addItem(round);
		}
		roundSelect.setValue(roundInfo);
		if (roundInfo != null && !roundInfo.getParticipants().isEmpty()) {
			fillTable();
			weightTable.setVisible(true);
		} else {
			weightTable.setVisible(false);
		}
	}

	private void fillTable() {
		weightTable.addContainerProperty("KW", Integer.class, null);
		for (String name : roundInfo.getParticipants()) {
			weightTable.addContainerProperty(name, BigDecimal.class, null);
		}

		int kw = roundInfo.getStartKw();

		while (kw <= roundInfo.getStartKw() + RoundInfo.ROUND_LENGTH) {
			Weight weight = weightEntryService.getLatestWeightEntry(roundInfo, kw);
			if (weight != null) {
				weightTable.addItem(createRow(weight), kw);
			}
			kw++;
		}
	}

	private Object[] createRow(Weight weight) {
		Object[] row = new Object[roundInfo.getParticipants().size() + 1];
		row[0] = weight.getKw();
		int i = 1;
		for (String name : roundInfo.getParticipants()) {
			row[i++] = weight.getWeightMap().get(name);
		}
		return row;
	}
}