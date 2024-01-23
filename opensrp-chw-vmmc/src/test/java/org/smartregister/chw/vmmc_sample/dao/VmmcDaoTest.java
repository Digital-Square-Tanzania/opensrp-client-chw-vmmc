package org.smartregister.chw.vmmc_sample.dao;

import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.smartregister.chw.vmmc.dao.VmmcDao;
import org.smartregister.repository.Repository;

import java.util.Date;

@RunWith(MockitoJUnitRunner.class)
public class VmmcDaoTest extends VmmcDao {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase database;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setRepository(repository);
    }

    @Test
    public void testIsRegisteredForVmmc() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        boolean registered = VmmcDao.isRegisteredForVmmc("12345");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT count(p.base_entity_id) count FROM ec_vmmc_enrollment p WHERE p.base_entity_id = '12345' AND p.is_closed = 0"), Mockito.any());
        Assert.assertFalse(registered);
    }

    @Test
    public void testGetVmmcTestDate() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        Date testDate = VmmcDao.getVmmcTestDate("34233");
        Mockito.verify(database).rawQuery(Mockito.contains("select vmmc_test_date from ec_vmmc_enrollment where base_entity_id = '34233'"), Mockito.any());
    }

    @Test
    public void testGetGentialExamination() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getGentialExamination("45678");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT genital_examination FROM ec_vmmc_services p  WHERE p.entity_id = '45678' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }


    @Test
    public void testGetSystolic() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getSystolic("78901");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT systolic FROM ec_vmmc_services p  WHERE p.entity_id = '78901' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetDiastolic() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getDiastolic("23456");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT diastolic FROM ec_vmmc_services p  WHERE p.entity_id = '23456' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }

    @Test
    public void testGetAnyComplaints() {
        Mockito.doReturn(database).when(repository).getReadableDatabase();
        String result = VmmcDao.getAnyComplaints("56789");
        Mockito.verify(database).rawQuery(Mockito.contains("SELECT any_complaints FROM ec_vmmc_services p  WHERE p.entity_id = '56789' ORDER BY last_interacted_with DESC LIMIT 1"), Mockito.any());
        Assert.assertNotNull(result);
    }
}

