/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Named;

/**
 *
 * @author dyihoon90
 */
@Named(value = "materialService")
@ApplicationScoped
public class MaterialService {

    private final static String[] matCategories;
    private final static List<Integer> genCategories;

    public MaterialService() {
    }

    static {

        matCategories = new String[25];
        matCategories[0] = "Chair";
        matCategories[1] = "Table";
        matCategories[2] = "Sofa";
        matCategories[3] = "Lighting";
        matCategories[4] = "Cupboard";
        matCategories[5] = "Organizer";
        matCategories[6] = "Shelf";
        matCategories[7] = "Boxes";
        matCategories[8] = "Basket";
        matCategories[9] = "Bed";
        matCategories[10] = "Matteress";
        matCategories[11] = "Rug";
        matCategories[12] = "Pillow";
        matCategories[13] = "Curtain";
        matCategories[14] = "Carpet";
        matCategories[15] = "Chest";
        matCategories[16] = "Painting";
        matCategories[17] = "Sink";
        matCategories[18] = "Faucet";
        matCategories[19] = "Screw";
        matCategories[20] = "Hinge";
        matCategories[21] = "Wood";
        matCategories[22] = "Steel";
        matCategories[23] = "Nail";
        matCategories[24] = "Plastic";

        genCategories = new ArrayList<>();
        genCategories.add(0); //Raw
        genCategories.add(1); //Living Room
        genCategories.add(2); //Bathroom
        genCategories.add(3); //Garden
        genCategories.add(4); //Kitchen
        genCategories.add(5); //Accessories
        genCategories.add(6); //Bedroom
        genCategories.add(7); //Children
        genCategories.add(8); //Seasonal
        genCategories.add(9); //Office
    }

    public List<String> getMatCategories() {
        return Arrays.asList(matCategories);
    }

    public List<Integer> getGenCategories() {
        return genCategories;
    }

}
