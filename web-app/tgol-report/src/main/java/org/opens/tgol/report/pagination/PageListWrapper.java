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
package org.opens.tgol.report.pagination;

import org.opens.tgol.presentation.data.PageResult;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.displaytag.decorator.TableDecorator;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * This wrapper class extends the TableDecorator class of the displayTag API
 * to customise the render of the displayed table regarding tgol specifications.
 *
 * @author jkowalczyk
 */
public class PageListWrapper extends TableDecorator {

    private static final String I18N_FILE_KEY = "page-list-page-I18N";
    private static final String PAGE_DETAILED_RESULT_KEY="pageList.pageDetailedResult";
    private static final String NO_DETAILED_RESULT_KEY="pageList.noDetailedResult";
    private static final String FOR_KEY="pageList.for";
    private static final char PERCENT_CHAR='%';

    /**
     * Default Constructor
     */
    public PageListWrapper() {
        super();
    }

    /**
     * Decoration of the displayed raw mark to add the appropriate grade css class.
     *
     * @return
     */
    public String getRawMark() {
        Object lObject = this.getCurrentRowObject();
        if (lObject instanceof PageResult){
            String gradeClass = getGradeClassFromMark(((PageResult)lObject).getRawMark());
            return "<div class=\""+gradeClass+"\"> " + ((PageResult)lObject).getRawMark() + PERCENT_CHAR + " </div>";
        }
        return "";
    }
    
    /**
     * Decoration of the displayed raw mark to add the appropriate grade css class.
     *
     * @return
     */
    public String getWeightedMark() {
        Object lObject = this.getCurrentRowObject();
        if (lObject instanceof PageResult){
            String gradeClass = getGradeClassFromMark(((PageResult)lObject).getWeightedMark());
            return "<div class=\""+gradeClass+"\"> " + ((PageResult)lObject).getWeightedMark() + PERCENT_CHAR + " </div>";
        }
        return "";
    }

    /**
     * Decoration of the detailed result link to add internationalised link with
     * appropriate internationalised title
     * @return
     */
    public String getDetailedResultLink() {
        Object lObject = this.getCurrentRowObject();
        ResourceBundle resourceBundle = getLang(this.getPageContext().getRequest());
        int lId = -1;
        String url = "";
        String mark = "";
        if (lObject instanceof PageResult){
            PageResult pageResult = ((PageResult)lObject);
            lId = pageResult.getId().intValue();
            url = pageResult.getUrl();
            mark = pageResult.getRawMark();
        }
        if (Integer.valueOf(mark) != 0 ) {
            return "<a href=\"audit-result.html?wr=" + lId + "\" title=\""+ getTitle(resourceBundle, url)+"\">"+getLink(resourceBundle)+"</a>";
        } else {
            return getNoDetailedResult(resourceBundle);
        }
    }

    /**
     * This method resolves the language through the request.
     * 
     * @param request
     * @return
     */
    private ResourceBundle getLang(ServletRequest request) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(I18N_FILE_KEY);;
        if (request instanceof HttpServletRequest) {
            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver((HttpServletRequest) request);

            if (localeResolver != null) {
                // get current locale
                Locale locale = localeResolver.resolveLocale((HttpServletRequest) request);
                resourceBundle = ResourceBundle.getBundle(I18N_FILE_KEY, locale);
            }
        }
        return resourceBundle;
    }

    /**
     * 
     * @param resourceBundle
     * @return
     */
    private String getNoDetailedResult(ResourceBundle resourceBundle) {
        return resourceBundle.getString(NO_DETAILED_RESULT_KEY);
    }

    /**
     *
     * @param resourceBundle
     * @return
     */
    private String getLink(ResourceBundle resourceBundle) {
        return resourceBundle.getString(PAGE_DETAILED_RESULT_KEY);
    }

    /**
     *
     * @param resourceBundle
     * @param url
     * @return
     */
    private String getTitle(ResourceBundle resourceBundle, String url) {
        return  getLink(resourceBundle) + 
                " " +
                resourceBundle.getString(FOR_KEY)+
                " " +
                url;
    }

    /**
     *
     * @param mark
     * @return
     */
    private String getGradeClassFromMark(String mark) {
        int markValue = Integer.valueOf(mark);
        if (markValue >= 90) {
            return "grade-a";
        } else if (markValue < 90 && markValue>=80) {
            return "grade-b";
        } else if (markValue<80 && markValue>=70) {
            return "grade-c";
        } else if (markValue<70 && markValue>=60) {
            return "grade-d";
        } else if (markValue<60 && markValue>=60) {
            return "grade-e";
        } else {
            return "grade-f";
        }
    }

}