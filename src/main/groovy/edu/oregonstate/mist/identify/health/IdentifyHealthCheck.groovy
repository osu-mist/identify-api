package edu.oregonstate.mist.identify.health

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result
import edu.oregonstate.mist.identify.db.IdentifyDAO

class IdentifyHealthCheck extends HealthCheck {
    private IdentifyDAO identifyDAO

    IdentifyHealthCheck(IdentifyDAO identifyDAO) {
        this.identifyDAO = identifyDAO
    }

    @Override
    protected Result check() {
        try {
            String status = identifyDAO.checkHealth()

            if (status != null) {
                return Result.healthy()
            }
            Result.unhealthy("status: ${status}")
        } catch(Exception e) {
            Result.unhealthy(e.message)
        }
    }
}