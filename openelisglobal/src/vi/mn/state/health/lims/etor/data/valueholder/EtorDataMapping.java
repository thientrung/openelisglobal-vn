/**
 * 
 */
package vi.mn.state.health.lims.etor.data.valueholder;

import us.mn.state.health.lims.common.valueholder.ValueHolder;
import us.mn.state.health.lims.common.valueholder.ValueHolderInterface;
import us.mn.state.health.lims.sample.valueholder.Sample;
import us.mn.state.health.lims.sampleitem.valueholder.SampleItem;

/**
 * @author dungtdo.sl
 *
 */
public class EtorDataMapping {
	private String etorId ;
/*	private String sampleId;*/
/*	private String sampleItemId;*/
	private int lisStatus; 
	private int etorStatus;
	private String etorUserId;
	private ValueHolderInterface sampleItem;
	private ValueHolderInterface sample;
	public EtorDataMapping() {
		this.sampleItem = new ValueHolder();
		this.sample = new ValueHolder();
		// TODO Auto-generated constructor stub
	}
/*	public EtorDataMapping(String id,String sId,String siId, String liS,String eS,String etorUId) {
		this.etorId= id;
		this.sampleId=sId;
		this.sampleItemId=siId;
		this.lisStatus=liS;
		this.etorStatus=eS;
		this.etorUserId=etorUId;
	}*/
	public String getEtorId() {
		return etorId;
	}
	public void setEtorId(String etorId) {
		this.etorId = etorId;
	}
/*	public String getSampleId() {
		return sampleId;
	}
	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}
	public String getSampleItemId() {
		return sampleItemId;
	}
	public void setSampleItemId(String sampleItemId) {
		this.sampleItemId = sampleItemId;
	}*/
	public String getEtorUserId() {
		return etorUserId;
	}
	public void setEtorUserId(String etorUserId) {
		this.etorUserId = etorUserId;
	}

	public SampleItem getSampleItem() {
		return (SampleItem) this.sampleItem.getValue();
	}

	public void setSampleItem(SampleItem sampleItem) {
		this.sampleItem.setValue(sampleItem);
	}

	public Sample getSample() {
		return (Sample) this.sample.getValue();
	}

	public void setSample(Sample sample) {
		this.sample.setValue(sample);
	}
	public int getLisStatus() {
		return lisStatus;
	}
	public void setLisStatus(int lisStatus) {
		this.lisStatus = lisStatus;
	}
	public int getEtorStatus() {
		return etorStatus;
	}
	public void setEtorStatus(int etorStatus) {
		this.etorStatus = etorStatus;
	}

	
}
