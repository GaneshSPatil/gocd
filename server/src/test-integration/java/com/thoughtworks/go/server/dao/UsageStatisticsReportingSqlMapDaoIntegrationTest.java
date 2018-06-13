package com.thoughtworks.go.server.dao;

import com.thoughtworks.go.domain.UsageStatisticsReporting;
import com.thoughtworks.go.server.dao.UsageStatisticsReportingSqlMapDao.DuplicateMetricReporting;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.UUID;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:WEB-INF/applicationContext-global.xml",
        "classpath:WEB-INF/applicationContext-dataLocalAccess.xml",
        "classpath:testPropertyConfigurer.xml"
})
public class UsageStatisticsReportingSqlMapDaoIntegrationTest {
    @Autowired
    private UsageStatisticsReportingSqlMapDao usageStatisticsReportingSqlMapDao;
    @Autowired
    private DatabaseAccessHelper dbHelper;

    @Before
    public void setup() throws Exception {
        dbHelper.onSetUp();
    }

    @After
    public void teardown() throws Exception {
        dbHelper.onTearDown();
    }

    @Test
    public void shouldSaveServerInformation() throws Exception {
        String serverId = UUID.randomUUID().toString();
        UsageStatisticsReporting usageStatisticsReporting = new UsageStatisticsReporting(serverId, new Date());
        Date statsUpdatedAt = new Date();
        usageStatisticsReporting.setLastReportedAt(statsUpdatedAt);
        usageStatisticsReportingSqlMapDao.saveOrUpdate(usageStatisticsReporting);

        usageStatisticsReporting = usageStatisticsReportingSqlMapDao.load();
        assertEquals(usageStatisticsReporting.getServerId(), serverId);
        assertTrue(usageStatisticsReporting.hasId());
        assertThat(usageStatisticsReporting.lastReportedAt().toInstant(), is(statsUpdatedAt.toInstant()));
    }

    @Test
    public void shouldUpdateServerInformation() throws Exception {
        String serverId = UUID.randomUUID().toString();
        UsageStatisticsReporting usageStatisticsReporting = new UsageStatisticsReporting(serverId, new Date());
        usageStatisticsReporting.setLastReportedAt(new Date());
        usageStatisticsReportingSqlMapDao.saveOrUpdate(usageStatisticsReporting);

        UsageStatisticsReporting toBeUpdated = usageStatisticsReportingSqlMapDao.load();
        toBeUpdated.setLastReportedAt(new Date(DateTime.now().minusDays(2).getMillis()));
        usageStatisticsReportingSqlMapDao.saveOrUpdate(toBeUpdated);

        UsageStatisticsReporting loaded = usageStatisticsReportingSqlMapDao.load();
        assertThat(loaded.lastReportedAt().toInstant(), not(is(usageStatisticsReporting.lastReportedAt().toInstant())));
        assertThat(loaded.lastReportedAt().toInstant(), is(toBeUpdated.lastReportedAt().toInstant()));
    }

    @Test
    public void shouldAllowOnlyOneInstanceOfServerStatsObjectInDB() throws Exception {
        UsageStatisticsReporting usageStatisticsReporting1 = new UsageStatisticsReporting("server-id", new Date());
        usageStatisticsReporting1.setLastReportedAt(new Date(DateTime.now().minusDays(2).getMillis()));
        usageStatisticsReportingSqlMapDao.saveOrUpdate(usageStatisticsReporting1);

        UsageStatisticsReporting usageStatisticsReporting2 = new UsageStatisticsReporting("server-id", new Date());
        usageStatisticsReporting2.setLastReportedAt(new Date(DateTime.now().minusDays(1).getMillis()));
        usageStatisticsReportingSqlMapDao.saveOrUpdate(usageStatisticsReporting2);

        UsageStatisticsReporting saved = usageStatisticsReportingSqlMapDao.load();
        assertThat(saved.lastReportedAt().toInstant(), is(usageStatisticsReporting2.lastReportedAt().toInstant()));
    }

    @Test
    public void shouldDisallowSavingServerStatsObjectWithADifferentIdIfAnInstanceAlreadyExistsInDb() throws Exception {
        UsageStatisticsReporting usageStatisticsReporting1 = new UsageStatisticsReporting("server-id", new Date());
        usageStatisticsReporting1.setLastReportedAt(new Date(DateTime.now().minusDays(2).getMillis()));
        usageStatisticsReportingSqlMapDao.saveOrUpdate(usageStatisticsReporting1);

        UsageStatisticsReporting usageStatisticsReporting2 = new UsageStatisticsReporting("server-id", new Date());
        usageStatisticsReporting2.setId(100);
        usageStatisticsReporting2.setLastReportedAt(new Date(DateTime.now().minusDays(1).getMillis()));

        Assertions.assertThrows(DuplicateMetricReporting.class, () -> usageStatisticsReportingSqlMapDao.saveOrUpdate(usageStatisticsReporting2));
    }
}
