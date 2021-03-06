/*
 * Copyright (c) 2015 Guillaume Hillairet.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Guillaume Hillairet - initial API and implementation
 */
package org.emfjson.jackson.junit.tests;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Before;
import org.junit.Test;

import org.emfjson.jackson.module.EMFModule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DynamicPackageTest {

	private ObjectMapper mapper = new ObjectMapper();

	@Before
	public void setUp() {
		mapper.registerModule(new EMFModule(new ResourceSetImpl()));
	}

	@Test
	public void testSavePackageWithTwoClasses() {
		JsonNode expected = mapper.createObjectNode()
			.put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EPackage")
			.put("name", "p")
			.put("nsURI", "http://foo.org/p")
			.set("eClassifiers", mapper.createArrayNode()
				.add(mapper.createObjectNode()
					.put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EClass")
					.put("name", "A"))
				.add(mapper.createObjectNode()
					.put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EClass")
					.put("name", "B")));

		EPackage p = EcoreFactory.eINSTANCE.createEPackage();
		p.setNsURI("http://foo.org/p");
		p.setName("p");

		EClass a = EcoreFactory.eINSTANCE.createEClass();
		a.setName("A");
		EClass b = EcoreFactory.eINSTANCE.createEClass();
		b.setName("B");

		p.getEClassifiers().add(a);
		p.getEClassifiers().add(b);

		assertEquals(expected, mapper.valueToTree(p));
	}

	@Test
	public void testLoadPackageWithTwoClasses() throws JsonProcessingException {
		JsonNode data = mapper.createObjectNode()
			.put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EPackage")
			.put("name", "p")
			.put("nsURI", "http://foo.org/p")
			.set("eClassifiers", mapper.createArrayNode()
				.add(mapper.createObjectNode()
					.put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EClass")
					.put("name", "A"))
				.add(mapper.createObjectNode()
					.put("eClass", "http://www.eclipse.org/emf/2002/Ecore#//EClass")
					.put("name", "B")));

		EPackage p = (EPackage) mapper.treeToValue(data, EObject.class);
		assertNotNull(p);

		assertEquals("p", p.getName());
		assertEquals("http://foo.org/p", p.getNsURI());
		assertEquals(2, p.getEClassifiers().size());

		assertEquals("A", p.getEClassifiers().get(0).getName());
		assertEquals("B", p.getEClassifiers().get(1).getName());
	}

}
