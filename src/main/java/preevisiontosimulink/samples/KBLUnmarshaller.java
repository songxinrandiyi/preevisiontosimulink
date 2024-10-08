package preevisiontosimulink.samples;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import preevisiontosimulink.parser.kblelements.CartesianPoint;
import preevisiontosimulink.parser.kblelements.CoreOccurrence;
import preevisiontosimulink.parser.kblelements.GeneralWireOccurrence;
import preevisiontosimulink.parser.kblelements.Harness;
import preevisiontosimulink.parser.kblelements.KBLContainer;

public class KBLUnmarshaller {
	public static void main(String[] args) {
		try {
			// File file = new File("BODY_CTRL_MDL_GEN_20240430 1.arxml");
			File file = new File("kbl/FRONT_BUMPER_RIGHT_20240529_LD.kbl");
			JAXBContext jaxbContext = JAXBContext.newInstance(KBLContainer.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

			JAXBElement<KBLContainer> rootElement = jaxbUnmarshaller.unmarshal(new StreamSource(file),
					KBLContainer.class);
			KBLContainer kblContainer = rootElement.getValue();

			/*
			 * List<CartesianPoint> cartesianPoints = kblContainer.getCartesianPoints();
			 * List<Node> nodes = kblContainer.getNodes();
			 * 
			 * for (Node node : nodes) { System.out.println("Node ID: " + node.getNodeId());
			 * String cartesianPointId = node.getCartesianPointId(); CartesianPoint
			 * correspondingCartesianPoint = findCartesianPoint(cartesianPoints,
			 * cartesianPointId); if (correspondingCartesianPoint != null) { List<Double>
			 * coordinates = correspondingCartesianPoint.getCoordinates();
			 * System.out.println("Coordinate: " + coordinates); } }
			 * 
			 * List<ConnectorHousing> connectorHousings =
			 * kblContainer.getConnectorHousings(); for (ConnectorHousing connectorHousing :
			 * connectorHousings) { System.out.println("Connector Housing ID: " +
			 * connectorHousing.getId()); System.out.println("Part Number: " +
			 * connectorHousing.getPartNumber()); System.out.println("Description: " +
			 * connectorHousing.getDescription());
			 * 
			 * // Check if Mass_information is present MassInformation massInformation =
			 * connectorHousing.getMassInformation(); if (massInformation != null) {
			 * System.out.println("Value Component: " +
			 * massInformation.getValueComponent()); }
			 * 
			 * // Print number of cavities Slots slots = connectorHousing.getSlots();
			 * System.out.println("Number of Cavities: " + slots.getCavities().size());
			 * 
			 * System.out.println(); // Adding a newline for readability }
			 */
			Harness harness = kblContainer.getHarness();
			List<GeneralWireOccurrence> generalWireOccurrences = harness.getGeneralWireOccurrences();
			for (GeneralWireOccurrence generalWireOccurrence : generalWireOccurrences) {
				System.out.println("GeneralWireOccurrence ID: " + generalWireOccurrence.getId());
				System.out.println("Type: " + generalWireOccurrence.getType());
				System.out.println("Part ID: " + generalWireOccurrence.getPart());
				if ("kbl:Special_wire_occurrence".equals(generalWireOccurrence.getType())) {
					System.out.println("Special Wire Id: " + generalWireOccurrence.getSpecialWireId());
					System.out.println("Number of Cores: " + generalWireOccurrence.getCoreOccurrences().size());
					for (CoreOccurrence coreOccurrence : generalWireOccurrence.getCoreOccurrences()) {
						System.out.println("Length of Core: "
								+ coreOccurrence.getLengthInformation().get(0).getLengthValue().getValueComponent());
					}
				}
				if ("kbl:Wire_occurrence".equals(generalWireOccurrence.getType())) {
					System.out.println("Wire Number: " + generalWireOccurrence.getWireNumber());
					System.out.println("Length Value: "
							+ generalWireOccurrence.getLengthInformation().get(0).getLengthValue().getValueComponent());
				}

				System.out.println(); // Adding a newline for readability
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private static CartesianPoint findCartesianPoint(List<CartesianPoint> cartesianPoints, String id) {
		for (CartesianPoint cartesianPoint : cartesianPoints) {
			if (cartesianPoint.getId().equals(id)) {
				return cartesianPoint;
			}
		}
		return null; // If no corresponding CartesianPoint is found
	}
}