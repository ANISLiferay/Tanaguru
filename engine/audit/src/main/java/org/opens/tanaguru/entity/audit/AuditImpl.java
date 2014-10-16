/*
 * Tanaguru - Automated webpage assessment
 * Copyright (C) 2008-2011  Open-S Company
 *
 * This file is part of Tanaguru.
 *
 * Tanaguru is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact us by mail: open-s AT open-s DOT com
 */
package org.opens.tanaguru.entity.audit;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.opens.tanaguru.entity.parameterization.Parameter;
import org.opens.tanaguru.entity.parameterization.ParameterImpl;
import org.opens.tanaguru.entity.reference.Test;
import org.opens.tanaguru.entity.reference.TestImpl;
import org.opens.tanaguru.entity.subject.WebResource;
import org.opens.tanaguru.entity.subject.WebResourceImpl;

/**
 * 
 * @author jkowalczyk
 */
@Entity
@Table(name = "AUDIT")
@XmlRootElement
public class AuditImpl implements Audit, Serializable {

	private static final long serialVersionUID = -9109080857144047795L;
	@Column(name = "Comment")
	private String comment;
	@OneToMany(mappedBy = "audit")
	private Set<ContentImpl> contentSet;
	@Temporal(javax.persistence.TemporalType.TIMESTAMP)
	@Column(name = "Dt_Creation")
	private Date dateOfCreation;
	@Column(name = "Manual_Audit_Dt_Creation")
	private Date manualAuditDateOfCreation;
	@OneToMany(mappedBy = "grossResultAudit")
	private Set<ProcessResultImpl> grossResultSet;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id_Audit")
	private Long id;
	@OneToMany(mappedBy = "netResultAudit")
	private Set<ProcessResultImpl> netResultSet;
	@Enumerated(EnumType.STRING)
	@Column(name = "Status")
	private AuditStatus status = AuditStatus.INITIALISATION;
	@OneToOne(mappedBy = "audit")
	private WebResourceImpl subject;
	@ManyToMany
	@JoinTable(name = "AUDIT_TEST", joinColumns = @JoinColumn(name = "Id_Audit"), inverseJoinColumns = @JoinColumn(name = "Id_Test"))
	private Set<TestImpl> testSet;
	@ManyToMany
	@JoinTable(name = "AUDIT_PARAMETER", joinColumns = @JoinColumn(name = "Id_Audit"), inverseJoinColumns = @JoinColumn(name = "Id_Parameter"))
	private Set<ParameterImpl> parameterSet;

	@Transient
	private String IdReconciliation;

	public AuditImpl() {
		super();
	}

	public AuditImpl(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	@Override
	public void addAllContent(Collection<Content> contentList) {
		for (Content content : contentList) {
			addContent(content);
		}
	}

	@Override
	public void addAllGrossResult(Collection<ProcessResult> pageResultList) {
		for (ProcessResult pageResult : pageResultList) {
			addGrossResult(pageResult);
		}
	}

	@Override
	public void addAllNetResult(Collection<ProcessResult> testResultList) {
		for (ProcessResult testResult : testResultList) {
			addNetResult(testResult);
		}
	}

	@Override
	public void addAllTest(Collection<Test> testList) {
		for (Test test : testList) {
			addTest(test);
		}
	}

	@Override
	public void addContent(Content content) {
		content.setAudit(this);
		if (contentSet == null) {
			contentSet = new HashSet<>();
		}
		this.contentSet.add((ContentImpl) content);
	}

	@Override
	public void addGrossResult(ProcessResult pageResult) {
		pageResult.setGrossResultAudit(this);
		if (grossResultSet == null) {
			grossResultSet = new HashSet<>();
		}
		grossResultSet.add((ProcessResultImpl) pageResult);
	}

	@Override
	public void addNetResult(ProcessResult testResult) {
		testResult.setNetResultAudit(this);
		if (netResultSet == null) {
			netResultSet = new HashSet<>();
		}
		netResultSet.add((ProcessResultImpl) testResult);
	}

	@Override
	public void setSubject(WebResource subject) {
		subject.setAudit(this);
		this.subject = (WebResourceImpl) subject;
	}

	@Override
	public void addTest(Test test) {
		this.testSet.add((TestImpl) test);
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	@XmlElementWrapper
	@XmlElementRefs({
			@XmlElementRef(type = org.opens.tanaguru.entity.audit.SSPImpl.class),
			@XmlElementRef(type = org.opens.tanaguru.entity.audit.JavascriptContentImpl.class),
			@XmlElementRef(type = org.opens.tanaguru.entity.audit.StylesheetContentImpl.class) })
	public Collection<Content> getContentList() {
		return (Collection) contentSet;
	}

	@Override
	public Date getDateOfCreation() {
		return dateOfCreation;
	}

	@Override
	public Date getManualAuditDateOfCreation() {
		return manualAuditDateOfCreation;
	}

	@Override
	@XmlElementWrapper
	@XmlElementRefs({
			@XmlElementRef(type = org.opens.tanaguru.entity.audit.IndefiniteResultImpl.class),
			@XmlElementRef(type = org.opens.tanaguru.entity.audit.DefiniteResultImpl.class) })
	public Collection<ProcessResult> getGrossResultList() {
		return (Collection) grossResultSet;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	@XmlElementWrapper
	@XmlElementRef(type = org.opens.tanaguru.entity.audit.DefiniteResultImpl.class)
	public Collection<ProcessResult> getNetResultList() {
		return (Collection) netResultSet;
	}

	@Override
	public AuditStatus getStatus() {
		return status;
	}

	@Override
	@XmlElementRefs({
			@XmlElementRef(type = org.opens.tanaguru.entity.subject.PageImpl.class),
			@XmlElementRef(type = org.opens.tanaguru.entity.subject.SiteImpl.class) })
	public WebResourceImpl getSubject() {
		return subject;
	}

	@Override
	@XmlElementWrapper
	@XmlElementRef(type = org.opens.tanaguru.entity.reference.TestImpl.class)
	public Collection<Test> getTestList() {
		return (Collection) testSet;
	}

	@Override
	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public void setContentList(Collection<Content> contentList) {
		if (this.contentSet == null) {
			this.contentSet = new HashSet<>();
		}
		for (Content content : contentList) {
			content.setAudit(this);
			this.contentSet.add((ContentImpl) content);
		}
	}

	@Override
	public void setDateOfCreation(Date dateOfCreation) {
		this.dateOfCreation = dateOfCreation;
	}

	@Override
	public void setManualAuditDateOfCreation(Date manualAuditDateOfCreation) {
		this.manualAuditDateOfCreation = manualAuditDateOfCreation;
	}

	@Override
	public void setGrossResultList(Collection<ProcessResult> pageResultList) {
		if (this.grossResultSet == null) {
			this.grossResultSet = new HashSet<>();
		}
		for (ProcessResult grossResult : pageResultList) {
			grossResult.setGrossResultAudit(this);
			this.grossResultSet.add((ProcessResultImpl) grossResult);
		}
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setNetResultList(Collection<ProcessResult> netResultList) {
		if (this.netResultSet == null) {
			this.netResultSet = new HashSet<>();
		}
		for (ProcessResult netResult : netResultList) {
			netResult.setNetResultAudit(this);
			this.netResultSet.add((ProcessResultImpl) netResult);
		}
	}

	@Override
	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	@Override
	public void setTestList(Collection<Test> testList) {
		if (this.testSet == null) {
			this.testSet = new HashSet<>();
		}
		for (Test test : testList) {
			this.testSet.add((TestImpl) test);
		}
	}

	@Override
	public void setParameterSet(Collection<Parameter> parameterSet) {
		if (this.parameterSet == null) {
			this.parameterSet = new HashSet<>();
		}
		for (Parameter param : parameterSet) {
			this.parameterSet.add((ParameterImpl) param);
		}
	}

	@Override
	public void addParameter(Parameter parameter) {
		if (this.parameterSet == null) {
			this.parameterSet = new HashSet<>();
		}
		this.parameterSet.add((ParameterImpl) parameter);
	}

	@Override
	public Collection<Parameter> getParameterSet() {
		if (this.parameterSet == null) {
			this.parameterSet = new HashSet<>();
		}
		return (Collection) parameterSet;
	}

	@Override
	public String getIdReconciliation() {
		return IdReconciliation;
	}

	@Override
	public void setIdReconciliation(String idReconciliation) {
		this.IdReconciliation = idReconciliation;

	}

}