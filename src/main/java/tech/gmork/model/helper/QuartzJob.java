package tech.gmork.model.helper;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.Data;

import java.time.Duration;

@Data
public class QuartzJob {

    private QuartzJob() {}

    private String name;
    private String interval;

    public static QuartzJobBuilder newBuilder() {
        return new QuartzJobBuilder();
    }

    public static class QuartzJobBuilder {

        private final QuartzJob job = new QuartzJob();

        private QuartzJobBuilder() {}

        public QuartzJobBuilder withName(String name) {
            job.setName(name);
            return this;
        }

        public QuartzJobBuilder withInterval(Duration interval) {
            float seconds = interval.toMillis() / 1000F;
            job.setInterval(seconds + "s");
            return this;
        }

        public QuartzJob build() {
            if (job.interval == null || job.name == null) {
                throw new WebApplicationException("QuartzJob didn't have all required parameters.", Response.Status.INTERNAL_SERVER_ERROR);
            }
            return job;
        }

    }
}