package org.smartregister.chw.vmmc_sample.presenter;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class BaseVmmcProfilePresenterTest {

    @Mock
    private BaseVmmcProfileContract.View view = Mockito.mock(BaseVmmcProfileContract.View.class);

    @Mock
    private BaseVmmcProfileContract.Interactor interactor = Mockito.mock(BaseVmmcProfileContract.Interactor.class);

    @Mock
    private VmmcMemberObject vmmcMemberObject = new VmmcMemberObject();

    private BaseVmmcProfilePresenter profilePresenter = new BaseVmmcProfilePresenter(view, interactor, vmmcMemberObject);


    @Test
    public void fillProfileDataCallsSetProfileViewWithDataWhenPassedMemberObject() {
        profilePresenter.fillProfileData(vmmcMemberObject);
        verify(view).setProfileViewWithData();
    }

    @Test
    public void fillProfileDataDoesntCallsSetProfileViewWithDataIfMemberObjectEmpty() {
        profilePresenter.fillProfileData(null);
        verify(view, never()).setProfileViewWithData();
    }

    @Test
    public void malariaTestDatePeriodIsLessThanSeven() {
        profilePresenter.recordVmmcButton("");
        verify(view).hideView();
    }

    @Test
    public void malariaTestDatePeriodGreaterThanTen() {
        profilePresenter.recordVmmcButton("OVERDUE");
        verify(view).setFollowUpButtonOverdue();
    }

    @Test
    public void malariaTestDatePeriodIsMoreThanFourteen() {
        profilePresenter.recordVmmcButton("EXPIRED");
        verify(view).hideView();
    }

    @Test
    public void refreshProfileBottom() {
        profilePresenter.refreshProfileBottom();
        verify(interactor).refreshProfileInfo(vmmcMemberObject, profilePresenter.getView());
    }

    @Test
    public void saveForm() {
        profilePresenter.saveForm(null);
        verify(interactor).saveRegistration(null, view);
    }
}
