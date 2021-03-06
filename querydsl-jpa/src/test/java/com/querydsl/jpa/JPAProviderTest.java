package com.querydsl.jpa;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import com.querydsl.jpa.impl.JPAProvider;

// 5.664
public class JPAProviderTest {

    private EntityManagerFactory factory;

    private EntityManager em;

    @After
    public void tearDown() {
        if (em != null) {
            em.close();
        }
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    public void hibernate() {
        factory = Persistence.createEntityManagerFactory("h2");
        em = factory.createEntityManager();
        System.out.println(em.getDelegate().getClass());
        assertEquals(HQLTemplates.DEFAULT, JPAProvider.getTemplates(em));
    }

    @Test
    public void hibernate_for_proxy() {
        factory = Persistence.createEntityManagerFactory("h2");
        em = factory.createEntityManager();
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(em, args);
            }
        };
        EntityManager proxy = (EntityManager) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{EntityManager.class},
                handler);
        assertEquals(HQLTemplates.DEFAULT, JPAProvider.getTemplates(proxy));
    }

    @Test
    public void eclipseLink() {
        factory = Persistence.createEntityManagerFactory("h2-eclipselink");
        em = factory.createEntityManager();
        System.out.println(em.getDelegate().getClass());
        System.out.println(em.getProperties());
        assertEquals(EclipseLinkTemplates.DEFAULT, JPAProvider.getTemplates(em));
    }

    @Test
    public void eclipseLink_for_proxy() {
        factory = Persistence.createEntityManagerFactory("h2-eclipselink");
        em = factory.createEntityManager();
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(em, args);
            }
        };
        EntityManager proxy = (EntityManager) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{EntityManager.class},
                handler);
        assertEquals(EclipseLinkTemplates.DEFAULT, JPAProvider.getTemplates(proxy));
    }

    @Test
    @Ignore // doesn't work on JDK 7
    public void openJPA() {
        factory = Persistence.createEntityManagerFactory("derby-openjpa");
        em = factory.createEntityManager();
        System.out.println(em.getDelegate().getClass());
        System.out.println(em.getProperties());
        assertEquals(OpenJPATemplates.DEFAULT, JPAProvider.getTemplates(em));
    }

    @Test
    @Ignore // temporarily ignored, since Batoo hangs on EntityManager creation
    public void batoo() {
        factory = Persistence.createEntityManagerFactory("h2-batoo");
        em = factory.createEntityManager();
        System.out.println(em.getDelegate().getClass());
        System.out.println(em.getProperties());
        assertEquals(BatooTemplates.DEFAULT, JPAProvider.getTemplates(em));
    }

}
