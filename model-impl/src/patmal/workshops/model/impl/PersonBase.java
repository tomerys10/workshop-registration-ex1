package patmal.workshops.model.impl;

import patmal.workshops.api.model.Person;

public abstract class PersonBase implements Person {

    private final String id;
    private final String name;
    private final String email;

    protected PersonBase(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getEmail() {
        return email;
    }
}
