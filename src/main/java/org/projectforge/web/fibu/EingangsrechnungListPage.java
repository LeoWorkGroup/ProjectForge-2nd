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

package org.projectforge.web.fibu;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.projectforge.common.DateHelper;
import org.projectforge.common.NumberHelper;
import org.projectforge.core.CurrencyFormatter;
import org.projectforge.fibu.EingangsrechnungDO;
import org.projectforge.fibu.EingangsrechnungDao;
import org.projectforge.fibu.EingangsrechnungsStatistik;
import org.projectforge.fibu.KontoCache;
import org.projectforge.fibu.KontoDO;
import org.projectforge.fibu.kost.KostZuweisungExport;
import org.projectforge.web.wicket.AbstractListPage;
import org.projectforge.web.wicket.CellItemListener;
import org.projectforge.web.wicket.CellItemListenerPropertyColumn;
import org.projectforge.web.wicket.CurrencyPropertyColumn;
import org.projectforge.web.wicket.DownloadUtils;
import org.projectforge.web.wicket.IListPageColumnsCreator;
import org.projectforge.web.wicket.ListPage;
import org.projectforge.web.wicket.ListSelectActionPanel;
import org.projectforge.web.wicket.RowCssClass;
import org.projectforge.web.wicket.components.ContentMenuEntryPanel;

@ListPage(editPage = EingangsrechnungEditPage.class)
public class EingangsrechnungListPage extends AbstractListPage<EingangsrechnungListForm, EingangsrechnungDao, EingangsrechnungDO> implements
IListPageColumnsCreator<EingangsrechnungDO>
{
  private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(EingangsrechnungListPage.class);

  private static final long serialVersionUID = 4417254962066648504L;

  @SpringBean(name = "eingangsrechnungDao")
  private EingangsrechnungDao eingangsrechnungDao;

  @SpringBean(name = "kontoCache")
  private KontoCache kontoCache;

  private EingangsrechnungsStatistik eingangsrechnungsStatistik;

  EingangsrechnungsStatistik getEingangsrechnungsStatistik()
  {
    if (eingangsrechnungsStatistik == null) {
      eingangsrechnungsStatistik = eingangsrechnungDao.buildStatistik(getList());
    }
    return eingangsrechnungsStatistik;
  }

  public EingangsrechnungListPage(final PageParameters parameters)
  {
    super(parameters, "fibu.eingangsrechnung");
  }

  public EingangsrechnungListPage(final ISelectCallerPage caller, final String selectProperty)
  {
    super(caller, selectProperty, "fibu.eingangsrechnung");
  }

  /**
   * Forces the statistics to be reloaded.
   * @see org.projectforge.web.wicket.AbstractListPage#refresh()
   */
  @Override
  public void refresh()
  {
    super.refresh();
    this.eingangsrechnungsStatistik = null;
  }

  @SuppressWarnings("serial")
  @Override
  public List<IColumn<EingangsrechnungDO, String>> createColumns(final WebPage returnToPage, final boolean sortable)
  {
    final List<IColumn<EingangsrechnungDO, String>> columns = new ArrayList<IColumn<EingangsrechnungDO, String>>();
    final CellItemListener<EingangsrechnungDO> cellItemListener = new CellItemListener<EingangsrechnungDO>() {
      public void populateItem(final Item<ICellPopulator<EingangsrechnungDO>> item, final String componentId,
          final IModel<EingangsrechnungDO> rowModel)
      {
        final EingangsrechnungDO eingangsrechnung = rowModel.getObject();
        appendCssClasses(item, eingangsrechnung.getId(), eingangsrechnung.isDeleted());
        if (eingangsrechnung.isDeleted() == true) {
          // Do nothing further
        } else if (eingangsrechnung.isUeberfaellig() == true) {
          appendCssClasses(item, RowCssClass.IMPORTANT_ROW);
        } else if (eingangsrechnung.isBezahlt() == false) {
          appendCssClasses(item, RowCssClass.BLUE);
        }
      }
    };
    columns.add(new CellItemListenerPropertyColumn<EingangsrechnungDO>(new Model<String>(getString("fibu.common.creditor")), getSortable(
        "kreditor", sortable), "kreditor", cellItemListener) {
      @SuppressWarnings({ "unchecked", "rawtypes"})
      @Override
      public void populateItem(final Item item, final String componentId, final IModel rowModel)
      {
        final EingangsrechnungDO eingangsrechnung = (EingangsrechnungDO) rowModel.getObject();
        String kreditor = StringEscapeUtils.escapeHtml(eingangsrechnung.getKreditor());
        if (form.getSearchFilter().isShowKostZuweisungStatus() == true) {
          final BigDecimal fehlBetrag = eingangsrechnung.getKostZuweisungFehlbetrag();
          if (NumberHelper.isNotZero(fehlBetrag) == true) {
            kreditor += " *** " + CurrencyFormatter.format(fehlBetrag) + " ***";
          }
        }
        final Label kreditorLabel = new Label(ListSelectActionPanel.LABEL_ID, kreditor);
        kreditorLabel.setEscapeModelStrings(false);
        item.add(new ListSelectActionPanel(componentId, rowModel, EingangsrechnungEditPage.class, eingangsrechnung.getId(), returnToPage,
            kreditorLabel));
        cellItemListener.populateItem(item, componentId, rowModel);
        addRowClick(item);
      }
    });
    columns.add(new CellItemListenerPropertyColumn<EingangsrechnungDO>(new Model<String>(getString("fibu.konto")), null, "konto",
        cellItemListener) {
      @SuppressWarnings({ "unchecked", "rawtypes"})
      @Override
      public void populateItem(final Item item, final String componentId, final IModel rowModel)
      {
        final EingangsrechnungDO rechnung = (EingangsrechnungDO) rowModel.getObject();
        final KontoDO konto = kontoCache.getKonto(rechnung.getKontoId());
        item.add(new Label(componentId, konto != null ? konto.formatKonto() : ""));
        cellItemListener.populateItem(item, componentId, rowModel);
      }
    });
    columns.add(new CellItemListenerPropertyColumn<EingangsrechnungDO>(getString("fibu.common.reference"),
        getSortable("referenz", sortable), "referenz", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<EingangsrechnungDO>(getString("fibu.rechnung.betreff"),
        getSortable("betreff", sortable), "betreff", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<EingangsrechnungDO>(getString("fibu.rechnung.datum.short"), getSortable("datum",
        sortable), "datum", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<EingangsrechnungDO>(getString("fibu.rechnung.faelligkeit.short"), getSortable(
        "faelligkeit", sortable), "faelligkeit", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<EingangsrechnungDO>(getString("fibu.rechnung.bezahlDatum.short"), getSortable(
        "bezahlDatum", sortable), "bezahlDatum", cellItemListener));
    columns.add(new CurrencyPropertyColumn<EingangsrechnungDO>(getString("fibu.common.netto"), getSortable("netSum", sortable), "netSum",
        cellItemListener));
    columns.add(new CurrencyPropertyColumn<EingangsrechnungDO>(getString("fibu.common.brutto"), getSortable("grossSum", sortable),
        "grossSum", cellItemListener));
    columns.add(new CellItemListenerPropertyColumn<EingangsrechnungDO>(new Model<String>(getString("comment")), getSortable("bemerkung",
        sortable), "bemerkung", cellItemListener));
    return columns;
  }

  @SuppressWarnings("serial")
  @Override
  protected void init()
  {
    dataTable = createDataTable(createColumns(this, true), "datum", SortOrder.DESCENDING);
    form.add(dataTable);
    final ContentMenuEntryPanel exportExcelButton = new ContentMenuEntryPanel(getNewContentMenuChildId(), new Link<Object>("link") {
      @Override
      public void onClick()
      {
        exportExcel();
      };
    }, getString("exportAsXls")).setTooltip(getString("tooltip.export.excel"));
    addContentMenuEntry(exportExcelButton);
  }

  void exportExcel()
  {
    refresh();
    final List<EingangsrechnungDO> rechnungen = getList();
    if (rechnungen == null || rechnungen.size() == 0) {
      // Nothing to export.
      form.addError("validation.error.nothingToExport");
      return;
    }
    final String filename = "ProjectForge-"
        + getString("fibu.common.creditor")
        + "_"
        + DateHelper.getDateAsFilenameSuffix(new Date())
        + ".xls";
    final byte[] xls = KostZuweisungExport.instance.exportRechnungen(rechnungen, getString("fibu.common.creditor"), kontoCache);
    if (xls == null || xls.length == 0) {
      log.error("Oups, xls has zero size. Filename: " + filename);
      return;
    }
    DownloadUtils.setDownloadTarget(xls, filename);
  }

  @Override
  protected EingangsrechnungListForm newListForm(final AbstractListPage< ? , ? , ? > parentPage)
  {
    return new EingangsrechnungListForm(this);
  }

  @Override
  protected EingangsrechnungDao getBaseDao()
  {
    return eingangsrechnungDao;
  }
}
