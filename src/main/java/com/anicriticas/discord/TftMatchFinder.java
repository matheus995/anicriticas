package com.anicriticas.discord;

import com.anicriticas.discord.messagebuilder.MessageBuilder;
import com.anicriticas.entities.match.tft.Character;
import com.anicriticas.enums.Region;
import com.anicriticas.service.TftAPIService;
import com.anicriticas.utils.CreateTftImageUtils;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.MessageCreateFields;
import discord4j.core.spec.MessageEditSpec;
import discord4j.rest.util.Color;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static com.anicriticas.utils.FileUtils.*;

@Component
public class TftMatchFinder {

    @Autowired
    GatewayDiscordClient client;

    private final TftAPIService tftAPIService = new TftAPIService();

    private Map<String, JSONObject> playersInGame = new HashMap<>();

    private Map<JSONObject, Snowflake> gamesAlreadyMessaged = new HashMap<>();

    private Snowflake messageId = null;

    private final String matchFindChannelId = System.getenv("MATCH_FIND_TFT_CHANNEL_ID");

    // Deixar fixedRate parametrizável
//    @Scheduled(fixedRate = 180000)
    public void TftMatchListener() {
        JSONArray playersInfo;

        //TODO buscar buscar.json de players apenas uma vez
//        URL urlGeneralEmojiFile = getResourceUrlByFileName("match-find-players-tft.json");
        URL urlGeneralEmojiFile = getResourceUrlByFileName("match-find-players-tft_all.json");
        playersInfo = getJsonArrayFromUrl(urlGeneralEmojiFile);

        for (int i = 0; i < playersInfo.toList().size(); i++) {
            JSONObject playerInfo = playersInfo.getJSONObject(i);

            String puuid = playerInfo.getString("puuid");
            Region accountRegion = Region.getByRegionName(playerInfo.getString("region"));

            JSONObject activeGame = tftAPIService.getActiveGamesByPuuid(puuid, accountRegion);

            // If player not in game, check the next player
            if (Objects.isNull(activeGame)) {
                playersInGame.remove(puuid);
                continue;
            }

            // If puuid from player it's in the Map playersInGame
            // And if gameId found is different from gameId that is in the Map, then it's a new game found, do a replacement in the Map
            // If gameId found equals to gameId in the Map, so it's the same game, check the next player
            if (playersInGame.containsKey(puuid)) {
                if (activeGame.getLong("gameId") != playersInGame.get(puuid).getLong("gameId")) {
                    playersInGame.replace(puuid, activeGame);
                }
                continue;
            }

            playersInGame.put(playerInfo.getString("puuid"), activeGame);
        }

        for (Map.Entry<String, JSONObject> gameInfo : playersInGame.entrySet()) {
            boolean messageAlreadySend = false;

            // If the match hasn't yet been notified on discord, then notify
            for (Map.Entry<JSONObject, Snowflake> gameMessaged : gamesAlreadyMessaged.entrySet()) {
                if (gameMessaged.getKey().getLong("gameId") == gameInfo.getValue().getLong("gameId")) {
                    messageAlreadySend = true;
                    break;
                }
            }

            if (messageAlreadySend) {
                continue;
            }

            JSONArray participants = new JSONArray(gameInfo.getValue().getJSONArray("participants"));
            JSONArray participantsRankedInfo = new JSONArray();

            Region matchRegion = Region.getRegionByEnumName(gameInfo.getValue().getString("platformId"));
            for (int i = 0; i < participants.toList().size(); i++) {
                JSONObject participant = participants.getJSONObject(i);

                JSONObject rankedInfo = getRankingTftSolo(tftAPIService.getRankedStats(participant.getString("puuid"), matchRegion));
                participantsRankedInfo.put(rankedInfo);
            }

            EmbedCreateSpec matchFoundMessageBuilder = EmbedCreateSpec.builder()
                    .color(Color.CYAN)
                    .author("Match Found - TFT", "", "")
                    .addField(MessageBuilder.getMatchFoundInformation(gameInfo.getValue()))
                    .addField(MessageBuilder.getTftMatchFoundPlayers(
                            gameInfo.getValue().getJSONArray("participants"),
                            participantsRankedInfo,
                            true)
                    )
                    .build();

            client.getChannelById(Snowflake.of(matchFindChannelId))
                    .ofType(MessageChannel.class)
                    .flatMap(channel -> channel.createMessage(matchFoundMessageBuilder)
                            .doOnNext(message -> messageId = message.getId()))
                    .block();

            gamesAlreadyMessaged.put(gameInfo.getValue(), messageId);
        }

        // If match finished, edit the original message with the result of the match
        for (Map.Entry<JSONObject, Snowflake> gameMessaged : gamesAlreadyMessaged.entrySet()) {

            Region matchRegion = Region.getRegionByEnumName(gameMessaged.getKey().getString("platformId"));
            String matchId = matchRegion.name() + "_" + gameMessaged.getKey().get("gameId");

            System.out.println("------------------------------------------------------------------------");
            System.out.printf("Pesquisando se partida foi finalizada. MatchId: %s | MessageId: %s\n", matchId, gameMessaged.getValue());

            // Search if match it's already finished
            JSONObject finishedMatch = tftAPIService.getFinishedMatchById(matchId, matchRegion);

            if (Objects.isNull(finishedMatch)) {
                System.out.printf("Partida nao foi finalizada. MatchId: %s | MessageId: %s\n", matchId, gameMessaged.getValue());
            } else {
                System.out.printf("Partida FOI finalizada. MatchId: %s | MessageId: %s\n", matchId, gameMessaged.getValue());
            }

            // If match finished, edit original message
            if (Objects.nonNull(finishedMatch)) {
                JSONObject player = getPlayerToCreateImage(finishedMatch.getJSONObject("info").getJSONArray("participants"), playersInfo);
                assert player != null;
                createTftImage(finishedMatch, player.getString("puuid"));

                String fileName = "tftImage.png";
                EmbedCreateSpec finishedMatchMessageBuilder = EmbedCreateSpec.builder()
                        .color(getColoByPlacement(player.getInt("placement")))
                        .author("TFT Match Finished", "", "")
                        .title(player.getInt("placement") + "th place")
                        .addField(MessageBuilder.getTftMatchInformation(finishedMatch, player))
                        .addField(MessageBuilder.getTftPlayerTraits(finishedMatch, player.getString("puuid")))
                        .image("attachment://" + fileName)
                        .build();

                editMessage(gameMessaged.getValue(), finishedMatchMessageBuilder, fileName);

                gamesAlreadyMessaged.remove(gameMessaged.getKey());
            }
        }
    }

//    @Scheduled(fixedRate = 180000)
//    public void TftMatchListener2() {
//        JSONObject finishedMatch;
//        JSONArray playersInfo;
//
////        JSONObject player = new JSONObject();
////        player.put("riotIdGameName", "mixmix");
////        player.put("riotIdTagline", "BR1");
////        player.put("puuid", "7Mx9gL0O5wWd11gxDZMmg6XdWmWPEJxBsjGjgar7ikvUokcK5ZoFXYv566UcYEgqtG_uN98azOb3pA");
////        player.put("placement", "2");
//
//        URL urlGeneralEmojiFile = getResourceUrlByFileName("match.json");
//        finishedMatch = getJsonObjectFromUrl(urlGeneralEmojiFile);
//
//        URL urlGeneralEmojiFile2 = getResourceUrlByFileName("match-find-players-tft.json");
//        playersInfo = getJsonArrayFromUrl(urlGeneralEmojiFile2);
//
//        JSONObject player = getPlayerToCreateImage(finishedMatch.getJSONObject("info").getJSONArray("participants"), playersInfo);
//        assert player != null;
//        createTftImage(finishedMatch, player.getString("puuid"));
//
//        String fileName = "tftImage.png";
//        EmbedCreateSpec finishedMatchMessageBuilder = EmbedCreateSpec.builder()
//                .color(getColoByPlacement(player.getInt("placement")))
//                .author("TFT Match Finished", "", "")
//                .title(player.getInt("placement") + "th place")
//                .addField(MessageBuilder.getTftMatchInformation(finishedMatch, player))
//                .addField(MessageBuilder.getTftPlayerTraits(finishedMatch, player.getString("puuid")))
//                .image("attachment://" + fileName)
//                .build();
//
//        editMessage(Snowflake.of("1407515041724497941"), finishedMatchMessageBuilder, fileName);
//    }

    private void editMessage(Snowflake messageId, EmbedCreateSpec newContent, String fileName) {
        client.getChannelById(Snowflake.of(matchFindChannelId))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.getMessageById(messageId))
                .flatMap(message -> {
                    try {
                        return message.edit(MessageEditSpec
                                .builder()
                                .addFile(fileName, new FileInputStream(fileName))
                                .embeds(Collections.singleton(newContent)).build());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();
    }

//    @Scheduled(fixedRate = 180000)
//    public void TftMatchListener2() {
//        JSONObject finishedMatch;
//        JSONObject player = new JSONObject();
//        player.put("riotIdGameName", "GameName");
//        player.put("riotIdTagline", "TagLine");
//
//        try {
//            URL urlGeneralEmojiFile = TftMatchFinder.class.getClassLoader().getResource("match.json");
//            finishedMatch = getJsonObjectFromUrl(urlGeneralEmojiFile);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        // Nome dos arquivos de imagem
//        String[] fileNames = {"tftImage.png", "tftImage.png"};
//
//        // Lista para armazenar os campos
//        List<EmbedCreateFields.Field> fields = new ArrayList<>();
//        // Adicionando os campos dinamicamente
//        fields.add(MessageBuilder.getTftMatchInformation(finishedMatch, player));
//        fields.add(MessageBuilder.getTftMatchInformation(finishedMatch, player));
//
//        // Lista para armazenar as imagens (ou URLs)
//        List<String> images = Arrays.asList("attachment://" + fileNames[0], "attachment://" + fileNames[1]);
//
//        // Criando o Embed
//        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder()
//                .color(Color.ENDEAVOUR)
//                .author("TFT Match Finished", "", "")
//                .title(1 + "th place");
//
//        // Adicionando os campos dinamicamente
//        for (EmbedCreateFields.Field field : fields) {
//            embedBuilder = embedBuilder.addField("Match Info", String.valueOf(field), false);
//        }
//
//        // Adicionando as imagens dinamicamente
//        for (String image : images) {
//            embedBuilder = embedBuilder.image(image);  // Adicionando cada imagem
//        }
//
//        EmbedCreateSpec finishedMatchMessageBuilder = embedBuilder.build();
//
//        // Enviar a mensagem com os campos e imagens dinâmicas
//        editMessage2(Snowflake.of("1357170767652978901"), finishedMatchMessageBuilder, fileNames);
//    }

    private void editMessage2(Snowflake messageId, EmbedCreateSpec newContent, String[] fileNames) {
        client.getChannelById(Snowflake.of(matchFindChannelId))
                .ofType(MessageChannel.class)
                .flatMap(channel -> channel.getMessageById(messageId))
                .flatMap(message -> {
                    try {
                        // Adicionando arquivos dinamicamente
                        List<MessageCreateFields.File> fileStreams = new ArrayList<>();
                        for (String fileName : fileNames) {
                            fileStreams.add(MessageCreateFields.File.of(fileName, new FileInputStream(fileName)));
                        }

                        MessageEditSpec.Builder embedBuilder = MessageEditSpec.builder();

                        // Adicionando os campos dinamicamente
                        for (MessageCreateFields.File file : fileStreams) {
                            embedBuilder = embedBuilder.addFile(file.name(), file.inputStream());
                        }

                        return message.edit(embedBuilder.embeds(Collections.singleton(newContent)).build());
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                })
                .block();
    }

    private void createTftImage(JSONObject finishedMatch, String participantToFind) {
        JSONArray matchParticipants = finishedMatch.getJSONObject("info").getJSONArray("participants");

        JSONObject participant = CreateTftImageUtils.getParticipantToCreateImage(participantToFind, matchParticipants);

        List<Character> characterList = new ArrayList<>();
        for (int j = 0; j < Objects.requireNonNull(participant).getJSONArray("units").toList().size(); j++) {
            JSONObject unit = participant.getJSONArray("units").getJSONObject(j);

            String characterName = unit.getString("character_id").substring(unit.getString("character_id").indexOf("_") + 1);

            int tier = unit.getInt("tier");
            int rarity = unit.getInt("rarity");

            List<String> items = new ArrayList<>();
            for (int i = 0; i < unit.getJSONArray("itemNames").toList().size(); i++) {
                String item = (String) unit.getJSONArray("itemNames").get(i);
                items.add(item);
            }
//                System.out.printf("%s | %d | %d | %s\n", characterName, tier, rarity, items);

            characterList.add(new Character(characterName, tier, rarity, items));
        }

        CreateTftImageUtils.createImage(participant, characterList);
    }

    private JSONObject getPlayerToCreateImage(JSONArray participants, JSONArray playersInfo) {

        for (int i = 0; i < participants.toList().size(); i++) {
            System.out.println("Player that will be find to create image: " + participants.getJSONObject(i).getString("riotIdGameName"));
            String participantUuid = participants.getJSONObject(i).getString("puuid");

            for (int j = 0; j < playersInfo.toList().size(); j++) {
                String playerInfoUuid = playersInfo.getJSONObject(j).getString("puuid");

                if (participantUuid.equals(playerInfoUuid)) {
                    return participants.getJSONObject(i);
                }
            }
        }
        return null;
    }

    private JSONObject getRankingTftSolo(JSONArray tftRankings) {
        for (int i = 0; i < tftRankings.toList().size(); i++) {
            JSONObject ranking = tftRankings.getJSONObject(i);

            if (ranking.getString("queueType").equals("RANKED_TFT")) {
                return ranking;
            }
        }
        return null;
    }

    private Color getColoByPlacement(int placement) {
        return switch (placement) {
            case 1, 2, 3, 4 -> Color.GREEN;
            default -> Color.RED;
        };
    }
}
