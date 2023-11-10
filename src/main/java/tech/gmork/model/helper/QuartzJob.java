package tech.gmork.model.helper;

import lombok.Data;
import org.quartz.JobDetail;
import org.quartz.Trigger;

@Data
public class QuartzJob {
    private JobDetail details;
    private Trigger trigger;

    public static QuartzJob fromJobAndTrigger(JobDetail details, Trigger trigger) {
        var job = new QuartzJob();
        job.setDetails(details);
        job.setTrigger(trigger);
        return job;
    }
}