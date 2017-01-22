package de.innovas.views;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
import de.innovas.util.WeightEval;

@SuppressWarnings("serial")
@SpringView(name = DefaultView.VIEW_NAME)
public class DefaultView extends VerticalLayout implements View {

	public static final String VIEW_NAME = "";

	private static int MAX_WEIGHT_INDEX = 99;

	@Autowired
	private WeightEntryService weightEntryService;
	@Autowired
	private RoundInfoService roundInfoService;

	private ComboBox roundSelect;
	private Map<Integer, RoundInfo> roundInfoMap;

	private RoundInfo roundInfo;
	private Table weightTable;

	private Map<String, WeightEval> roundEvalMap;

	@PostConstruct
	void init() {

		roundSelect = new ComboBox("Runde");
		roundSelect.setIcon(FontAwesome.REPEAT);
		roundSelect.setNullSelectionAllowed(false);

		roundInfoMap = new HashMap<>();

		roundInfoMap = roundInfoService.getAllAsMap();

		if (!roundInfoMap.isEmpty()) {
			roundInfo = roundInfoMap.get(Collections.max(roundInfoMap.keySet()));
			roundSelect.addValueChangeListener(event -> {
				roundInfo = (RoundInfo) roundSelect.getValue();
				roundEvalMap = weightEntryService.getWeightEvalMap(roundInfo);
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
		weightTable.setCellStyleGenerator((source, itemId, propertyId) -> {
			String name = (String) propertyId;
			BigDecimal weight = (roundInfo.getParticipants().contains(name))
					? (BigDecimal) source.getItem(itemId).getItemProperty(propertyId).getValue() : null;
			Integer kw = itemId != null ? (Integer) itemId : null;
			if (kw.compareTo(MAX_WEIGHT_INDEX) == 0)
				return "normal";
			if (name == null || weight == null)
				return null;
			WeightEval eval = roundEvalMap.get(name);
			if (kw > eval.getMinWeightKw() && weight.compareTo(getWeightWithMargin(eval.getMinWeight())) == 1) {
				return "red";
			}
			return null;
		});

		// String name = (String) propertyId;
		// BigDecimal weight = (roundInfo.getParticipants().contains(name))
		// ? (BigDecimal)
		// source.getItem(itemId).getItemProperty(propertyId).getValue() : null;
		// if (weight != null &&
		// weight.compareTo(getWeightWithMargin(minWeightMap.get(name))) == 1) {
		// return "red";
		// }
		// return null;

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
				weightTable.addItem(createRow(weight.getWeightMap(), kw, false), kw);
			}
			kw++;
		}

		weightTable.addItem(createRow(getMinWeightMap(), null, true), MAX_WEIGHT_INDEX);
	}

	private Map<String, BigDecimal> getMinWeightMap() {
		Map<String, BigDecimal> minWeightMap = new HashMap<>();
		for (Entry<String, WeightEval> entry : roundEvalMap.entrySet()) {
			minWeightMap.put(entry.getKey(), entry.getValue().getMinWeight());
		}
		return minWeightMap;
	}

	private Object[] createRow(Map<String, BigDecimal> weightMap, Integer kw, boolean margin) {
		Object[] row = new Object[roundInfo.getParticipants().size() + 1];
		row[0] = kw;
		int i = 1;
		for (String name : roundInfo.getParticipants()) {
			BigDecimal value = weightMap.get(name);
			row[i++] = (margin && value != null) ? getWeightWithMargin(value) : value;
		}
		return row;
	}

	private BigDecimal getWeightWithMargin(BigDecimal weight) {
		return weight.multiply(BigDecimal.valueOf(1.015)).setScale(1, BigDecimal.ROUND_HALF_UP);
	}
}