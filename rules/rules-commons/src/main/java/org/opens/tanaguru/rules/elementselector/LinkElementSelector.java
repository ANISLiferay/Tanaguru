/*
 *  Tanaguru - Automated webpage assessment
 *  Copyright (C) 2008-2013  Open-S Company
 * 
 *  This file is part of Tanaguru.
 * 
 *  Tanaguru is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 * 
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 *  Contact us by mail: open-s AT open-s DOT com
 */

package org.opens.tanaguru.rules.elementselector;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.opens.tanaguru.processor.SSPHandler;
import org.opens.tanaguru.ruleimplementation.ElementHandler;
import org.opens.tanaguru.ruleimplementation.ElementHandlerImpl;
import static org.opens.tanaguru.rules.keystore.AttributeStore.TITLE_ATTR;
import org.opens.tanaguru.rules.keystore.CssLikeQueryStore;
import static org.opens.tanaguru.rules.keystore.CssLikeQueryStore.TEXT_LINK_CSS_LIKE_QUERY;
import org.opens.tanaguru.rules.keystore.HtmlElementStore;
import org.opens.tanaguru.rules.textbuilder.LinkTextElementBuilder;
import org.springframework.util.CollectionUtils;

/**
 * Element selector implementation that select text links (without children tags)
 * 
 * @author jkowalczyk
 */
public class LinkElementSelector implements ElementSelector {

    /** 
     * The list of elements that are considered as context of the link. 
     * The presence of the td element in that list enables to deal with the case
     * where the context is handled by a table header as defined in the rule
     */ 
    private static final String[] PARENT_CONTEXT_ELEMENTS_TAB = {
                HtmlElementStore.P_ELEMENT, 
                HtmlElementStore.H1_ELEMENT, 
                HtmlElementStore.H2_ELEMENT, 
                HtmlElementStore.H3_ELEMENT, 
                HtmlElementStore.H4_ELEMENT, 
                HtmlElementStore.H5_ELEMENT, 
                HtmlElementStore.H6_ELEMENT, 
                HtmlElementStore.LI_ELEMENT, 
                HtmlElementStore.TD_ELEMENT, 
                };
    
    /** */
    private static final String[] PREV_SIBLING_CONTEXT_ELEMENTS_TAB = {
                HtmlElementStore.H1_ELEMENT, 
                HtmlElementStore.H2_ELEMENT, 
                HtmlElementStore.H3_ELEMENT, 
                HtmlElementStore.H4_ELEMENT, 
                HtmlElementStore.H5_ELEMENT, 
                HtmlElementStore.H6_ELEMENT, 
                };

    /** */
    private static Collection<String> PARENT_CONTEXT_ELEMENTS = 
            CollectionUtils.arrayToList(PARENT_CONTEXT_ELEMENTS_TAB);
    
    /** */
    private static Collection<String> PREV_SIBLING_CONTEXT_ELEMENTS = 
            CollectionUtils.arrayToList(PREV_SIBLING_CONTEXT_ELEMENTS_TAB);
    
    /** */
    private ElementHandler<Element> decidableElements = new ElementHandlerImpl();
    public ElementHandler<Element> getDecidableElements() {
        return decidableElements;
    }

    /** */
    private ElementHandler<Element> notDecidableElements = new ElementHandlerImpl();
    public ElementHandler<Element> getNotDecidableElements() {
        return notDecidableElements;
    }
    
    /* 
     * does the selection split results between the one that have a context 
     and the one that have not
     */
    private boolean considerContext = true;
    public boolean isConsiderContext() {
        return considerContext;
    }
    
    /* The element builder needed to build the link text */
    private LinkTextElementBuilder linkTextElementBuilder = 
            new LinkTextElementBuilder();
    
    /**
     * Default constructor
     */
    public LinkElementSelector(boolean considerContext) {
        this.considerContext = considerContext;
    }

    /**
     * 
     * @return 
     */
    protected String getCssLikeQuery() {
        return TEXT_LINK_CSS_LIKE_QUERY;
    }
    
    @Override
    public void selectElements(SSPHandler sspHandler, ElementHandler<Element> elementHandler) {
        Elements elements = sspHandler.beginCssLikeSelection().
                               domCssLikeSelectNodeSet(getCssLikeQuery()).
                               getSelectedElements();
        characteriseElements(elements);
        elementHandler.addAll(notDecidableElements.get());
        elementHandler.addAll(decidableElements.get());
    }

    /**
     * 
     * @param elementHandler 
     */
    protected void characteriseElements(Elements elements) {
        for (Element el : elements) {
            characteriseElement(el);
        }
    }
    
    /**
     * 
     * @param element 
     */
    protected void characteriseElement(Element element) {
        String linkText = getLinkText(element);
        if (!isLinkPartOfTheScope(element, linkText)) {
            return;
        }
        if (considerContext) {
            if (doesLinkHaveContext(element, linkText)) {
                notDecidableElements.add(element);
            } else {
                decidableElements.add(element);
            }
        } else {
            decidableElements.add(element);   
        }
    }

        /**
     * 
     * @param linkElement
     * @return 
     */
    protected String getLinkText(Element linkElement) {
        return linkTextElementBuilder.buildTextFromElement(linkElement);
    }
    
    /**
     * 
     * @param linkElement
     * @param linkText
     * @return 
     */
    protected boolean isLinkPartOfTheScope(Element linkElement, String linkText) {
        return StringUtils.isNotBlank(linkText);
    }
    
    /**
     * 
     * @param linkElement
     * @param linkText
     * @return 
     */
    protected boolean doesLinkHaveContext(Element linkElement, String linkText) {
        if (linkElement.hasAttr(TITLE_ATTR) && 
                !StringUtils.equals(linkElement.attr(TITLE_ATTR), linkText)) {
            return true;
        }
        if (StringUtils.isNotBlank(linkElement.parent().ownText())) {
            return true;
        }
        if (doesPreviousSiblingContainsHeading(linkElement)) {
            return true;
        }
        for (Element parent : linkElement.parents()) {
            if (PARENT_CONTEXT_ELEMENTS.contains(parent.tagName()) || 
                    doesPreviousSiblingContainsHeading(parent)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * @param element
     * @return 
     */
    private boolean doesPreviousSiblingContainsHeading(Element element) {
        Element prevElementSibling = element.previousElementSibling();
        while (prevElementSibling != null) {
            if (PREV_SIBLING_CONTEXT_ELEMENTS.contains(prevElementSibling.tagName()) || 
                    !prevElementSibling.select(CssLikeQueryStore.HEADINGS_CSS_LIKE_QUERY).isEmpty()) {
                return true;
            }
            prevElementSibling = prevElementSibling.previousElementSibling();
        }
        return false;
    }
    
}