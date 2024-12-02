package tech.gmork.model.dtos;

import lombok.Data;
import tech.gmork.model.entities.Deployment;
import tech.gmork.model.enums.ChangeType;

@Data
public class ClientTask {
    ChangeType changeType;
    Deployment deployment;
}
