package us.mn.state.health.lims.reports.action.implementation;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import us.mn.state.health.lims.common.action.BaseActionForm;
import us.mn.state.health.lims.common.util.IdValuePair;

public class ParameterProjectDengue implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String label;
	private final List<IdValuePair> list;
	private String selection;

	public ParameterProjectDengue(List<IdValuePair> list, String label) {
		this.label = label;
		this.list = list;
	}

	public void setRequestParameters(BaseActionForm dynaForm) {
		try {
			PropertyUtils.setProperty(dynaForm, "listProjectDengue", this);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public String getLabel() {
		return label;
	}

	public List<IdValuePair> getList() {
		return list;
	}

	public String getSelection() {
		return selection;
	}

	public void setSelection(String selection) {
		this.selection = selection;
	}

	public String getSelectionAsName() {
		String selection = getSelection();

		for (IdValuePair pair : getList()) {
			if (selection.equals(pair.getId())) {
				return pair.getValue();
			}
		}

		return "";
	}
}
