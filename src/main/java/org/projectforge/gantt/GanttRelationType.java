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

package org.projectforge.gantt;

import org.projectforge.core.I18nEnum;

public enum GanttRelationType implements I18nEnum
{
  START_START("start_start"), START_FINISH("start_finish"), FINISH_START("finish_start"), FINISH_FINISH("finish_finish");

  private String key;

  public boolean isIn(final GanttRelationType... types)
  {
    for (final GanttRelationType type : types) {
      if (this == type) {
        return true;
      }
    }
    return false;

  }

  /**
   * The key will be used e. g. for i18n.
   */
  public String getKey()
  {
    return key;
  }

  public String getI18nKey()
  {
    return "gantt.relationType." + key;
  }

  GanttRelationType(final String key)
  {
    this.key = key;
  }
}