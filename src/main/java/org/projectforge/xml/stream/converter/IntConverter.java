/////////////////////////////////////////////////////////////////////////////
//
// Project ProjectForge Community Edition
//         www.projectforge.org
//
// Copyright (C) 2001-2013 Kai Reinhard (k.reinhard@micromata.de)
//
// ProjectForge is dual-licensed.
//
// This community edition is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License as published
// by the Free Software Foundation; version 3 of the License.
//
// This community edition is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
// Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, see http://www.gnu.org/licenses/.
//
/////////////////////////////////////////////////////////////////////////////

package org.projectforge.xml.stream.converter;

import org.apache.commons.lang.StringUtils;
import org.projectforge.xml.stream.XmlConstants;

public class IntConverter extends AbstractValueConverter<Integer>
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(IntConverter.class);

  @Override
  public Integer fromString(String str)
  {
    try {
      if (StringUtils.isEmpty(str) == true || XmlConstants.NULL_IDENTIFIER.equals(str) == true) {
        return null;
      }
      return new Integer(str);
    } catch (final NumberFormatException ex) {
      log.warn("Can't convert value '" + str + "' to int.");
      return 0;
    }
  }
}