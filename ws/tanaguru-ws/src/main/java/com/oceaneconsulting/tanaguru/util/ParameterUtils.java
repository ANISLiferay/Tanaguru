package com.oceaneconsulting.tanaguru.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.opens.tanaguru.entity.parameterization.Parameter;
import org.opens.tanaguru.entity.service.parameterization.ParameterDataService;

import org.apache.commons.lang.StringUtils;
import com.oceaneconsulting.tanaguru.ws.types.AuditSiteOrder;

/**
 * Parameters initialization utilities.
 * 
 * This class defines sets of parameters used in different webservice's operations.
 * 
 * @author shamdi at oceaneconsulting dot com
 *
 */
public class ParameterUtils {
	
	/**
	 * Class logger definition
	 */
	private static final Logger LOGGER = Logger.getLogger(ParameterUtils.class);
	
	
	
	public static Map<String, List<Parameter>> parametersMap = null;
	
	public static void  initParametersMap(ParameterDataService parameterDataService) {
		
		// create map for parameters identified par parent element code.
		if (parametersMap == null) {
			
			// get all parameters
			Collection<Parameter> params = parameterDataService.findAll();


			parametersMap = new HashMap<String, List<Parameter>>();


			for (Parameter param : params) {
				if (param != null && param.getParameterElement() != null && param.getParameterElement().getParameterElementCode() != null) {
					if (!parametersMap.containsKey(param.getParameterElement().getParameterElementCode())) {
						parametersMap.put(param.getParameterElement().getParameterElementCode(), new ArrayList<Parameter>());
					}
					param.setDefaultParameterValue(Boolean.FALSE);
					parametersMap.get(param.getParameterElement().getParameterElementCode()).add(param);
				}
			}
		}
	}
	
	/**
	 * Common parameters definition with default values. 
	 * @return set of parameters needed in audit request.
	 */
	private static Set<Parameter> commonDefaultParameters() {
		
		Set<Parameter> parameters = new HashSet<Parameter>();
		
		parameters.add(createParameter(ParameterInputs.DATA_TABLE_MARKER, ""));
		parameters.add(createParameter(ParameterInputs.PROXY_HOST, ""));
		parameters.add(createParameter(ParameterInputs.CONSIDER_COOKIES, "true"));
		parameters.add(createParameter(ParameterInputs.ALTERNATIVE_CONTRAST_MECHANISM, "false"));
		parameters.add(createParameter(ParameterInputs.INFORMATIVE_IMAGE_MARKER, ""));
		parameters.add(createParameter(ParameterInputs.PROXY_PORT, ""));
		parameters.add(createParameter(ParameterInputs.PRESENTATION_TABLE_MARKER, ""));
		parameters.add(createParameter(ParameterInputs.DECORATIVE_IMAGE_MARKER, ""));
		
		return parameters;
	}
	
	/**
	 * Create parameter based on minimal information.
	 * @param code : parameter element code
	 * @param value : parameter value
	 * @return initialized parameter
	 */
    public static Parameter createParameter(String code, String value) {
        
       
        Parameter parameter = null;
        
        //Parameter identification
        if(parametersMap != null && parametersMap.containsKey(code)){
        	List<Parameter> parameters = parametersMap.get(code);
        	for(Parameter param : parameters){
        		if(value.equals(param.getValue())){//second level identification of parameter
        			parameter = param;
        		} 
        	}
        	if(parameter == null){ //
        		parameter = parameters.get(0);
        	}
        } 
        
		
		if (parameter != null) {
			parameter.setDefaultParameterValue(Boolean.TRUE);
			parameter.setValue(value);
		}

		return parameter;
    }
    
	/**
	 * Page audit parameters definition with default values. 
	 * @return set of parameters needed in page audit request.
	 */
	public static Set<Parameter> getDefaultParametersForPA(){
		
		Set<Parameter> parameters = commonDefaultParameters();
		
		//specific parameters to page audit
		parameters.add(createParameter(ParameterInputs.MAX_DOCUMENTS, "1"));
		parameters.add(createParameter(ParameterInputs.MAX_DURATION, "86400"));
		parameters.add(createParameter(ParameterInputs.EXCLUSION_REGEXP, ""));
		parameters.add(createParameter(ParameterInputs.DEPTH, "0"));
		
		return parameters;
	}
	/**
	 * Site audit parameters definition with default values. 
	 * @return set of parameters needed in site audit request.
	 */
	public static Set<Parameter> getDefaultParametersForSite(){
		
		Set<Parameter> parameters = commonDefaultParameters();
		
		//specific parameters to page audit
		parameters.add(createParameter(ParameterInputs.MAX_DOCUMENTS, "10000"));
		parameters.add(createParameter(ParameterInputs.MAX_DURATION, "86400"));
		parameters.add(createParameter(ParameterInputs.EXCLUSION_REGEXP, ""));
		parameters.add(createParameter(ParameterInputs.DEPTH, "1"));
		
		return parameters;
	}
	/**
	 * Scenario audit parameters definition with default values. 
	 * @return set of parameters needed in scenario audit request.
	 */
	public static Set<Parameter> getDefaultParametersForScenario() {
		return commonDefaultParameters();
	}
	

	public static void initializePAInputOptions(String level, String tblMarker, String prTblMarker, String dcrImgMarker, String infImgMarker, Set<Parameter> parameters) {
		
		if(level != null && !level.isEmpty()){
			parameters.add(ParameterUtils.createParameter(ParameterInputs.LEVEL, level));
		} 

		for(Parameter parameter : parameters){
			
			if(parameter != null && parameter.getParameterElement() != null && parameter.getParameterElement().getParameterElementCode() != null){

    			if(tblMarker != null){
	    			if(ParameterInputs.DATA_TABLE_MARKER.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(tblMarker);
	    			}
    			}
    			if(prTblMarker != null){ 
	    			if(ParameterInputs.PRESENTATION_TABLE_MARKER.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(prTblMarker);
	    			}
    			}
    			if(dcrImgMarker != null){
	    			if(ParameterInputs.DECORATIVE_IMAGE_MARKER.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(dcrImgMarker);
	    			}
    			}
    			if(infImgMarker != null){
	    			if(ParameterInputs.INFORMATIVE_IMAGE_MARKER.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(infImgMarker);
	    			}
    			}

    			LOGGER.debug(parameter.getParameterElement().getShortLabel() +  " = "+ parameter.getValue());
			}
		}
	}
	public static void initializeSAInputOptions(AuditSiteOrder auditSiteOrder, Set<Parameter> parameters){
		
		if(auditSiteOrder.getLevel() != null && !auditSiteOrder.getLevel() .isEmpty()){
			parameters.add(ParameterUtils.createParameter(ParameterInputs.LEVEL, auditSiteOrder.getLevel()));
		} 
	
		for(Parameter parameter : parameters){
			
			if(parameter != null && parameter.getParameterElement() != null && parameter.getParameterElement().getParameterElementCode() != null){
    			if(StringUtils.isNotBlank(auditSiteOrder.getTblMarker())){
	    			if(ParameterInputs.DATA_TABLE_MARKER.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(auditSiteOrder.getTblMarker());
	    			}
    			}
    			if(StringUtils.isNotBlank(auditSiteOrder.getPrTblMarker())){ 
	    			if(ParameterInputs.PRESENTATION_TABLE_MARKER.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(auditSiteOrder.getPrTblMarker());
	    			}
    			} 
    			if(StringUtils.isNotBlank(auditSiteOrder.getDcrImgMarker())){
	    			if(ParameterInputs.DECORATIVE_IMAGE_MARKER.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(auditSiteOrder.getDcrImgMarker());
	    			}
    			}
    			if(StringUtils.isNotBlank(auditSiteOrder.getInfImgMarker())){
	    			if(ParameterInputs.INFORMATIVE_IMAGE_MARKER.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(auditSiteOrder.getInfImgMarker());
	    			}
    			}
    			if(StringUtils.isNotBlank(auditSiteOrder.getMaxDocuments())){
	    			if(ParameterInputs.MAX_DOCUMENTS.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(auditSiteOrder.getMaxDocuments());
	    			}
    			}
    			if(StringUtils.isNotBlank(auditSiteOrder.getMaxDuration())){
	    			if(ParameterInputs.MAX_DURATION.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(auditSiteOrder.getMaxDuration());
	    			}
    			}
    			if(StringUtils.isNotBlank(auditSiteOrder.getExclusionRegExp())){
	    			if(ParameterInputs.EXCLUSION_REGEXP.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(auditSiteOrder.getExclusionRegExp());
	    			}
    			}
    			if(StringUtils.isNotBlank(auditSiteOrder.getDepth())){
	    			if(ParameterInputs.DEPTH.equals(parameter.getParameterElement().getParameterElementCode()) ){
	    				parameter.setValue(auditSiteOrder.getDepth());
	    			}
    			}
    			LOGGER.debug(parameter.getParameterElement().getShortLabel() +  " = "+ parameter.getValue());
			}
		}
	}
			
}
