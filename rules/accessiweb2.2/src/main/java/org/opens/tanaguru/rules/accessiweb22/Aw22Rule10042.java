/*
 * Tanaguru - Automated webpage assessment
 * Copyright (C) 2008-2013  Open-S Company
 *
 * This program is free software: you can redistribute it and/or modify
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

package org.opens.tanaguru.rules.accessiweb22;

import org.opens.tanaguru.ruleimplementation.AbstractPageRuleCssImplementation;
import org.opens.tanaguru.rules.csschecker.ForbiddenUnitChecker;

/**
 * Implementation of the rule 10.4.2 of the referential Accessiweb 2.2.
 * <br/>
 * For more details about the implementation, refer to <a href="http://www.tanaguru.org/en/content/aw22-rule-10-4-2">the rule 10.4.2 design page.</a>
 * @see <a href="http://www.accessiweb.org/index.php/accessiweb-22-english-version.html#test-10-4-2"> 10.4.2 rule specification</a>
 *
 * @author jkowalczyk
 */

public class Aw22Rule10042 extends AbstractPageRuleCssImplementation {

    /* the font-size css property key */
    private static final String FONT_SIZE_CSS_PROPERTY = "font-size";
    
    /**
     * 
     */
    public Aw22Rule10042() {
        super(new ForbiddenUnitChecker(FONT_SIZE_CSS_PROPERTY));
    }

}