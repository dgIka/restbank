package com.example.bankcards.entity;

import jakarta.persistence.MappedSuperclass;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@MappedSuperclass
public abstract class AbstractEntity<ID> {

    protected abstract ID getId();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;

        Class<?> thisClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        Class<?> otherClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();

        if (thisClass != otherClass) return false;

        AbstractEntity<?> other = (AbstractEntity<?>) o;
        return getId() != null && Objects.equals(getId(), other.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
