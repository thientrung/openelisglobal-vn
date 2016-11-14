package vi.mn.state.health.lims.dataexchange;

import java.util.List;

import us.mn.state.health.lims.common.exception.LIMSRuntimeException;
import vi.mn.state.health.lims.dataexchange.valueholder.OrderVO;

public interface DataExchange {
    /**
     * Get list structure Fields
     * 
     * @return
     */
	public List<Object[]> getStructureFields();
	/**
	 * Add New order about sample infor + analysis infor+ and patient infor
	 * @throws LIMSRuntimeException
	 */
	public boolean addNewOrder(List<OrderVO> order) throws LIMSRuntimeException;
}
