package com.anicriticas.utils;

import com.anicriticas.entities.tft.Character;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateTftImageUtils {

    private static final String TFT_IMAGE_NAME = "tftImage.png";
    private static final String TFT_STAR_IMAGES_DIR = "tftStarImages/";
    private static final String TFT_CHARACTER_IMAGES_DIR = "tftCharacterImages/";
    private static final String TFT_ITEM_IMAGES_DIR = "tftItemImages/";

    public static void createImage(JSONObject participant, List<Character> characterList) {

        try {
            BufferedImage greyStar = ImageIO.read(new File(TFT_STAR_IMAGES_DIR + "grey-star.png"));
            BufferedImage randomItem = ImageIO.read(new File(TFT_ITEM_IMAGES_DIR + "TFT_Item_BlueBuff.png"));
            BufferedImage firstCharacterImage = ImageIO.read(new File(TFT_CHARACTER_IMAGES_DIR + characterList.getFirst().getName() + ".png"));

            int spacing = 10;
            int starSpacing = 5;
            int starHeight = greyStar.getHeight();
            int height = starHeight + starSpacing + firstCharacterImage.getHeight() + randomItem.getHeight();
            int width = (firstCharacterImage.getWidth() + spacing) * participant.getJSONArray("units").toList().size();

            // Create new image
            BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = combined.createGraphics();

            int xOffset = 0;

            for (Character character : characterList) {
                System.out.println("Pesquisando imagem do character: " + character.getName());
                BufferedImage characterImage = ImageIO.read(new File(TFT_CHARACTER_IMAGES_DIR + character.getName() + ".png"));

                if (Objects.isNull(characterImage)) {
                    characterImage = ImageIO.read(new File(TFT_CHARACTER_IMAGES_DIR + "Unknown.png"));
                }

                g.drawImage(championImage(
                                character,
                                characterImage,
                                getItemsImageFromCharacter(character.getItems()),
                                getStarByCharacterRarity(character.getRarity()), width, height, starSpacing),
                        xOffset,
                        0,
                        null
                ); // Champion image
                xOffset += characterImage.getWidth() + spacing;
            }

            g.dispose();

            // Save the final imageF
            ImageIO.write(combined, "PNG", new File(TFT_IMAGE_NAME));

            System.out.println("Image successfully created!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage championImage(Character character, BufferedImage champion, List<BufferedImage> items, BufferedImage star, int imageHeight, int imageWidth, int starSpacing) {

        // Create new image
        BufferedImage combined = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();

        int xOffset = 0;

        // Middle with 1 images
        if (character.getTier() == 1) {
            xOffset = (champion.getWidth() / 2) - (star.getWidth() / 2);
        }
        // Middle with 2 images
        else if (character.getTier() == 2) {
            xOffset = (champion.getWidth() / 2) - (star.getWidth());
        }
        // Middle with 3 images
        else if (character.getTier() == 3) {
            xOffset = star.getWidth() / 2;
        }

        // Star image
        for (int i = 0; i < character.getTier(); i++) {
            g.drawImage(star, xOffset, 0, null);
            xOffset += star.getWidth();
        }

        g.drawImage(champion, 0, star.getHeight() + starSpacing, null); // Champion image

        g.setColor(getColorByRarity(character.getRarity()));
        g.setStroke(new BasicStroke(3));
        g.drawRect(0, star.getHeight() + starSpacing, champion.getWidth(), champion.getHeight());

        if (!items.isEmpty()) {
            g.drawImage(itemsImage(items, champion.getWidth()), 0, star.getHeight() + starSpacing + champion.getHeight(), null); // Items image
        }

        g.dispose();

        return combined;
    }

    public static BufferedImage itemsImage(List<BufferedImage> items, int championWidth) {
        int height = items.getFirst().getHeight();

        // Create new image
        BufferedImage combined = new BufferedImage(championWidth, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();

        int xOffset = 0;

        // Middle with 1 image
        if (items.size() == 1) {
            xOffset = (championWidth / 2) - (items.getFirst().getWidth() / 2);
        }
        // Middle with 2 images
        else if (items.size() == 2) {
            xOffset = (championWidth / 3) - (items.getFirst().getWidth() / 2);
        }

        for (BufferedImage item : items) {
            g.drawImage(item, xOffset, 0, null);
            xOffset += item.getWidth();
        }

        g.dispose();

        return combined;
    }

    private static Color getColorByRarity(int rank) {
        return switch (rank) {
            case 1 -> Color.GREEN;
            case 2 -> Color.BLUE;
            case 3, 4 -> Color.MAGENTA;
            case 5, 6, 7, 8, 9 -> Color.YELLOW;
            default -> Color.GRAY;
        };
    }

    private static BufferedImage getStarByCharacterRarity(int rarity) throws IOException {
        return switch (rarity) {
            case 1 -> ImageIO.read(new File(TFT_STAR_IMAGES_DIR + "green-star.png"));
            case 2 -> ImageIO.read(new File(TFT_STAR_IMAGES_DIR + "blue-star.png"));
            case 3, 4 -> ImageIO.read(new File(TFT_STAR_IMAGES_DIR + "pink-star.png"));
            case 5, 6, 7, 8, 9 -> ImageIO.read(new File(TFT_STAR_IMAGES_DIR + "gold-star.png"));
            default -> ImageIO.read(new File(TFT_STAR_IMAGES_DIR + "grey-star.png"));
        };
    }

    public static JSONObject getParticipantToCreateImage(String participantUuid, JSONArray participants) {
        for (int i = 0; i < participants.toList().size(); i++) {
            JSONObject participant = participants.getJSONObject(i);

            if (participant.get("puuid").equals(participantUuid)) {
                return participant;
            }
        }
        return null;
    }

    public static List<BufferedImage> getItemsImageFromCharacter(List<String> items) {
        List<BufferedImage> itemsImage = new ArrayList<>();

        try {
            for (String item : items) {
                BufferedImage itemImage = ImageIO.read(new File("tftItemsImages/" + item + ".png"));
                itemsImage.add(itemImage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return itemsImage;
    }

}
