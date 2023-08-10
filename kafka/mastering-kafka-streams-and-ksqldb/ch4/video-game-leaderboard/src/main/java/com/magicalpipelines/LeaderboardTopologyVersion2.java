package com.magicalpipelines;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.ValueJoiner;

import com.magicalpipelines.join.Enriched;
import com.magicalpipelines.join.ScoreWithPlayer;
import com.magicalpipelines.model.Player;
import com.magicalpipelines.model.Product;
import com.magicalpipelines.model.ScoreEvent;
import com.magicalpipelines.serialization.json.JsonSerdes;

public class LeaderboardTopologyVersion2 {
  public static Topology build() {
    StreamsBuilder builder = new StreamsBuilder();

    // register the score events stream
    KStream<String, ScoreEvent> scoreEvents = builder
        .stream("score-events", Consumed.with(Serdes.ByteArray(), JsonSerdes.ScoreEvent()))
        .selectKey((k, v) -> v.getPlayerId().toString());
    // The selectKey operator allows us to rekey records. This is often needed
    // to meet the co-partitioning requirements for performing certain types of
    // joins.
    // selectKey is used to rekey records. In this case, it helps us meet the first
    // co partitioning requirement of ensuring records on both sides of the join
    // (the
    // score-events data and players data) are keyed by the same field

    // create the sharded players table
    KTable<String, Player> players = builder.table("players", Consumed.with(Serdes.String(), JsonSerdes.Player()));

    GlobalKTable<String, Product> products = builder.globalTable("products",
        Consumed.with(Serdes.String(), JsonSerdes.Product()));

    // join params for scoreEvents -> players join
    Joined<String, ScoreEvent, Player> playerJoinParams = Joined.with(Serdes.String(), JsonSerdes.ScoreEvent(),
        JsonSerdes.Player());

    // join scoreEvents -> players
    ValueJoiner<ScoreEvent, Player, ScoreWithPlayer> scorePlayerJoiner = (score, player) -> new ScoreWithPlayer(score,
        player);
    // we need to use a ValueJoiner to specify how different records should be
    // combined. A ValueJoiner
    // simply takes each record that is involved in the join, and produces a new,
    // combined record
    KStream<String, ScoreWithPlayer> withPlayers = scoreEvents.join(players, scorePlayerJoiner, playerJoinParams);

    /**
     * map score-with-player records to products
     *
     * <p>
     * Regarding the KeyValueMapper param types: - String is the key type for the
     * score events
     * stream - ScoreWithPlayer is the value type for the score events stream -
     * String is the lookup
     * key type
     */

    KeyValueMapper<String, ScoreWithPlayer, String> keyMapper = (leftKey, scoreWithPlayer) -> {
      return String.valueOf(scoreWithPlayer.getScoreEvent().getProductId());
    };

    // join the withPlayers stream to the product global ktable
    ValueJoiner<ScoreWithPlayer, Product, Enriched> productJoiner = (scoreWithPlayer,
        product) -> new Enriched(scoreWithPlayer, product);
    KStream<String, Enriched> withProducts = withPlayers.join(products, keyMapper, productJoiner);
    withProducts.print(Printed.<String, Enriched>toSysOut().withLabel("with-products"));

    /** Group the enriched product stream */
    KGroupedStream<String, Enriched> grouped = withProducts.groupBy(
        (key, value) -> value.getProductId().toString(),
        Grouped.with(Serdes.String(), JsonSerdes.Enriched()));
    // alternatively, use the following if you want to name the grouped repartition topic:
    // Grouped.with("grouped-enriched", Serdes.String(), JsonSerdes.Enriched()))

    return builder.build();
  }
}