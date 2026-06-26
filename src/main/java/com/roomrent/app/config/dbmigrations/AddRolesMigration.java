package com.roomrent.app.config.dbmigrations;

import com.roomrent.app.domain.Authority;
import com.roomrent.app.security.AuthoritiesConstants;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

@ChangeUnit(id = "add-arrendador-arrendatario-roles", order = "002")
public class AddRolesMigration {

    private final MongoTemplate template;

    public AddRolesMigration(MongoTemplate template) {
        this.template = template;
    }

    @Execution
    public void changeSet() {
        Authority arrendador = new Authority();
        arrendador.setName(AuthoritiesConstants.ARRENDADOR);
        template.save(arrendador);

        Authority arrendatario = new Authority();
        arrendatario.setName(AuthoritiesConstants.ARRENDATARIO);
        template.save(arrendatario);
    }

    @RollbackExecution
    public void rollback() {}
}
