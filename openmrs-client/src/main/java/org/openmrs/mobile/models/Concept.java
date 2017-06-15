/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.mobile.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Concept extends BaseOpenmrsObject {
	@SerializedName("datatype")
	@Expose
	private Datatype datatype;

	@SerializedName("description")
	@Expose
	private Object description;

	@SerializedName("conceptClass")
	@Expose
	private ConceptClass conceptClass;
	@SerializedName("answers")
	@Expose
	private List<ConceptAnswer> answers;
	@SerializedName("names")
	@Expose
	private List<ConceptName> names;
	private String preferredName;
	private List<ConceptMap> conceptMappings;

	public Datatype getDatatype() {
		return datatype;
	}

	public void setDatatype(Datatype datatype) {
		this.datatype = datatype;
	}

	public Object getDescription() {
		return description;
	}

	public void setDescription(Object description) {
		this.description = description;
	}

	public ConceptClass getConceptClass() {
		return conceptClass;
	}

	public void setConceptClass(ConceptClass conceptClass) {
		this.conceptClass = conceptClass;
	}

	public List<ConceptAnswer> getAnswers() {
		return answers;
	}

	public void setAnswers(List<ConceptAnswer> answers) {
		this.answers = answers;
	}

	public List<ConceptName> getNames() {
		return names;
	}

	public void setNames(List<ConceptName> names) {
		this.names = names;
	}

	public String getPreferredName() {
		return preferredName;
	}

	public void setPreferredName(String preferredName) {
		this.preferredName = preferredName;
	}

	public List<ConceptMap> getConceptMappings() {
		return conceptMappings;
	}

	public void setConceptMappings(List<ConceptMap> conceptMappings) {
		this.conceptMappings = conceptMappings;
	}

	@Override
	public String toString() {
		return getDisplay();
	}
}
