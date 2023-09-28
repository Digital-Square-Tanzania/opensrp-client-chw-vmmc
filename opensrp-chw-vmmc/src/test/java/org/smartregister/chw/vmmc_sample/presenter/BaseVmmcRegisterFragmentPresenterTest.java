package org.smartregister.chw.vmmc_sample.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.chw.vmmc.presenter.BaseVmmcRegisterFragmentPresenter;
import org.smartregister.chw.vmmc.util.Constants;
import org.smartregister.chw.vmmc.util.DBConstants;
import org.smartregister.configurableviews.model.View;

import java.util.Set;
import java.util.TreeSet;

public class BaseVmmcRegisterFragmentPresenterTest {
    @Mock
    protected BaseVmmcRegisterFragmentContract.View view;

    @Mock
    protected BaseVmmcRegisterFragmentContract.Model model;

    private BaseVmmcRegisterFragmentPresenter baseVmmcRegisterFragmentPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        baseVmmcRegisterFragmentPresenter = new BaseVmmcRegisterFragmentPresenter(view, model, "");
    }

    @Test
    public void assertNotNull() {
        Assert.assertNotNull(baseVmmcRegisterFragmentPresenter);
    }

    @Test
    public void getMainCondition() {
        Assert.assertEquals("", baseVmmcRegisterFragmentPresenter.getMainCondition());
    }

    @Test
    public void getDueFilterCondition() {
        Assert.assertEquals(" (cast( julianday(STRFTIME('%Y-%m-%d', datetime('now'))) -  julianday(IFNULL(SUBSTR(malaria_test_date,7,4)|| '-' || SUBSTR(malaria_test_date,4,2) || '-' || SUBSTR(malaria_test_date,1,2),'')) as integer) between 7 and 14) ", baseVmmcRegisterFragmentPresenter.getDueFilterCondition());
    }

    @Test
    public void getDefaultSortQuery() {
        Assert.assertEquals(VmmcConstants.TABLES.VMMC_REGISTER + "." + DBConstants.KEY.LAST_INTERACTED_WITH + " DESC ", baseVmmcRegisterFragmentPresenter.getDefaultSortQuery());
    }

    @Test
    public void getMainTable() {
        Assert.assertEquals(VmmcConstants.TABLES.VMMC_REGISTER, baseVmmcRegisterFragmentPresenter.getMainTable());
    }

    @Test
    public void initializeQueries() {
        Set<View> visibleColumns = new TreeSet<>();
        baseVmmcRegisterFragmentPresenter.initializeQueries(null);
        Mockito.doNothing().when(view).initializeQueryParams("ec_malaria_confirmation", null, null);
        Mockito.verify(view).initializeQueryParams("ec_malaria_confirmation", null, null);
        Mockito.verify(view).initializeAdapter(visibleColumns);
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
    }

}