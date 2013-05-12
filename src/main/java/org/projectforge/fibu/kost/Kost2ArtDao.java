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

package org.projectforge.fibu.kost;

import java.util.List;

import org.hibernate.criterion.Order;
import org.projectforge.core.BaseDao;
import org.projectforge.core.BaseSearchFilter;
import org.projectforge.core.QueryFilter;
import org.projectforge.user.UserRightId;


public class Kost2ArtDao extends BaseDao<Kost2ArtDO>
{
  public static final UserRightId USER_RIGHT_ID = UserRightId.FIBU_COST_UNIT;

  private KostCache kostCache;

  public Kost2ArtDao()
  {
    super(Kost2ArtDO.class);
    avoidNullIdCheckBeforeSave = true;
    userRightId = USER_RIGHT_ID;
  }
  
  @Override
  protected void afterSaveOrModify(Kost2ArtDO obj)
  {
    super.afterSaveOrModify(obj);
    kostCache.updateKost2Arts();
  }

  @Override
  public List<Kost2ArtDO> getList(BaseSearchFilter filter)
  {
    QueryFilter queryFilter = new QueryFilter(filter);
    queryFilter.addOrder(Order.asc("id"));
    return getList(queryFilter);
  }

  /**
   * id != null && id &gt;= null;
   * @see org.projectforge.core.BaseDao#isIdValid(java.lang.Integer)
   */
  @Override
  protected boolean isIdValid(Integer id)
  {
    return (id != null && id >= 0);
  }
  
  public void setKostCache(KostCache kostCache)
  {
    this.kostCache = kostCache;
  }

  @Override
  public Kost2ArtDO newInstance()
  {
    return new Kost2ArtDO();
  }
}
