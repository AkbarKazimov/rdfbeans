package org.cyberborean.rdfbeans.test.namedgraphs;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cyberborean.rdfbeans.test.RDFBeansTestBase;
import org.cyberborean.rdfbeans.test.examples.entities.IPerson;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.junit.Before;
import org.junit.Test;


public class ExampleIFaceTest extends RDFBeansTestBase {

    IPerson john;
    IPerson mary;
    IPerson jim;
    
    Resource subject;
    
    IRI graph0;
    IRI graph1;
    IRI graph2;
    
    @Before
    public void setUp() throws Exception {    	    	
    	
    	graph0 = manager.getRepositoryConnection().getValueFactory().createIRI("urn:contexts:0");
		graph1 = manager.getRepositoryConnection().getValueFactory().createIRI("urn:contexts:1");
		graph2 = manager.getRepositoryConnection().getValueFactory().createIRI("urn:contexts:2");
		
    	john = manager.getContext(graph1).create("johndoe", IPerson.class);
        john.setName("John Doe");
        john.setEmail("johndoe@example.com");        
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 1980);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        john.setBirthday(c.getTime());
        String[] nicks = new String[] {"johndoe", "johnnydoe", "JohnnyTheTerrible"};
        john.setNick(nicks);        
        john.setHomepage(new URI("http://johndoe.example.com"));
        
        mary = manager.getContext(graph1).create("marysmith", IPerson.class); 
        mary.setName("Mary Smith");
        mary.setEmail("marysmith@example.com");
        Set<IPerson> maryKnows = new HashSet<IPerson>();        
        maryKnows.add(john); // recursive link
        mary.setKnows(maryKnows);
        
        jim = manager.getContext(graph1).create("jimsmith", IPerson.class);
        jim.setName("Jim Smith");
        jim.setEmail("jimsmith@example.com");
        Set<IPerson> jimKnows = new HashSet<IPerson>();
        jimKnows.add(mary);
        jimKnows.add(john); // recursive link
        jim.setKnows(jimKnows);
        
        Set<IPerson> johnKnows = new HashSet<IPerson>();
        johnKnows.add(mary);
        johnKnows.add(jim);
        john.setKnows(johnKnows);        
    }

    @Test
    public void testGetters() throws Exception { 
    	john = manager.getContext(graph1).create("johndoe", IPerson.class);
    	assertEquals(john.getName(), "John Doe");
    	assertEquals(john.getEmail(), "johndoe@example.com");    	
    	assertNotNull(john.getBirthday());
    	Calendar c = Calendar.getInstance();
    	c.setTime(john.getBirthday());
    	assertEquals(c.get(Calendar.YEAR), 1980);
    	assertEquals(c.get(Calendar.MONTH), 0);
    	assertEquals(c.get(Calendar.DAY_OF_MONTH), 1);    	
    	String[] nicks = john.getNick();
    	assertEquals(nicks.length, 3);
    	assertTrue(Arrays.equals(nicks, new String[] {"johndoe", "johnnydoe", "JohnnyTheTerrible"}));
    	for (int i = 0; i < nicks.length; i++) {
    		assertEquals(nicks[i], john.getNick(i));
    	}    	
    	assertEquals(john.getHomepage(), new URI("http://johndoe.example.com"));
    	Collection<IPerson> johnKnows = john.getKnows();
    	assertFalse(johnKnows.isEmpty());
    	assertEquals(johnKnows.size(), 2);
    	for (IPerson p: johnKnows) {
    		assertTrue(p.equals(mary) || p.equals(jim));
    		assertNotNull(p.getName());
    		assertNotNull(p.getEmail());
    		assertTrue(p.getName().equals("Mary Smith") || p.getName().equals("Jim Smith"));
    		assertFalse(p.getKnows().isEmpty());
    		assertTrue(p.getKnows().contains(john));
    	}
    	
    	john = manager.create("johndoe", IPerson.class);
    	assertNull(john.getName());
    	
    	john = manager.getContext(graph0).create("johndoe", IPerson.class);
    	assertNull(john.getName());
    }
    
    
    @Test
    public void testUpdate() throws Exception {
    	john = manager.getContext(graph1).create("johndoe", IPerson.class);
    	assertEquals(john.getName(), "John Doe");
    	john.setName("John Doe II");
    	assertEquals(john.getName(), "John Doe II");
    	// Indexed properties
    	john.setNick(1, "johnnydoeII");
    	assertEquals(john.getNick(1), "johnnydoeII");
    	assertTrue(Arrays.equals(john.getNick(), new String[] {"johndoe", "johnnydoeII", "JohnnyTheTerrible"}));
    	
    	john = manager.create("johndoe", IPerson.class);
    	assertNull(john.getName());
    	
    	john = manager.getContext(graph0).create("johndoe", IPerson.class);
    	assertNull(john.getName());
    }
   
}
