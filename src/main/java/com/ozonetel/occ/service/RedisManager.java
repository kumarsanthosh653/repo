/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ozonetel.occ.service;

import java.util.List;
import java.util.Set;
import redis.clients.jedis.resps.Tuple;

/**
 *
 * @author pavanj
 * @param <T>
 */
public interface RedisManager<T> {

    public String save(String key, String field, T obj);

    public T get(String key, String field);

    public String saveAsJson(String key, T obj);

    public T getFromJson(String key);

    public String saveex(String key, int seconds, T obj);

    public Long addToSet(String set, String member);

    public Long deleteFromSet(String set, String member);

    /**
     * Move the specifided member from the setString at srckey to the setString
     * at dstkey. This operation is atomic, in every given moment the element
     * will appear to be in the source or destination setString for accessing
     * clients.
     * <p>
     * If the source setString does not exist or does not contain the specified
     * element no operation is performed and zero is returned, otherwise the
     * element is removed from the source setString and added to the destination
     * setString. On success one is returned, even if the element was already
     * present in the destination setString.
     * <p>
     * An error is raised if the source or destination keys contain a non Set
     * value.
     * <p>
     * Time complexity O(1)
     *
     * @param srckey
     * @param dstkey
     * @param member
     * @return Integer reply, specifically: 1 if the element was moved 0 if the
     * element was not found on the first setString and no operation was
     * performed
     */
    public Long moveSet(String originSet, String destinationSet, String member);

    /**
     * Set the string value as value of the key. The string can't be longer than
     * 1073741824 bytes (1 GB).
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param value
     * @return Status code reply
     */
    public String setString(final String key, String value);

    /**
     * Get the value of the specified key. If the key does not exist the special
     * value 'nil' is returned. If the value stored at key is not a string an
     * error is returned because GET can only handle string values.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @return Bulk reply
     */
    public String getString(String key);

    /**
     * Add the specified member having the specifeid score to the sorted
     * setString stored at key. If member is already a member of the sorted
     * setString the score is updated, and the element reinserted in the right
     * position to ensure sorting. If key does not exist a new sorted setString
     * with the specified member as sole member is crated. If the key exists but
     * does not hold a sorted setString value an error is returned.
     * <p>
     * The score value can be the string representation of a double precision
     * floating point number.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the
     * sorted setString
     *
     * @param key
     * @param score
     * @param member
     * @return Integer reply, specifically: 1 if the new element was added 0 if
     * the element was already a member of the sorted setString and the score
     * was updated
     */
    public Long zadd(final String key, final double score, final String member);

    /**
     * Return the score of the specified element of the sorted setString at key.
     * If the specified element does not exist in the sorted setString, or the
     * key does not exist at all, a special 'nil' value is returned.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @param key
     * @param member
     * @return the score
     */
    public Double zscore(final String key, final String member);

    /**
     * If member already exists in the sorted setString adds the increment to
     * its score and updates the position of the element in the sorted setString
     * accordingly. If member does not already exist in the sorted setString it
     * is added with increment as score (that is, like if the previous score was
     * virtually zero). If key does not exist a new sorted setString with the
     * specified member as sole member is crated. If the key exists but does not
     * hold a sorted setString value an error is returned.
     * <p>
     * The score value can be the string representation of a double precision
     * floating point number. It's possible to provide a negative value to
     * perform a decrement.
     * <p>
     * For an introduction to sorted sets check the Introduction to Redis data
     * types page.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the
     * sorted setString
     *
     * @param key
     * @param score
     * @param member
     * @return The new score
     */
    public Double zincby(final String key, final Double score, final String member);

    public Long setrange(String key, long offset, String value);

    public String getrange(String key, long startOffset, long endOffset);

    /**
     * Test if the specified key exists. The command returns "1" if the key
     * exists, otherwise "0" is returned. Note that even keys setString with an
     * empty string as value will return "1".
     *
     * Time complexity: O(1)
     *
     * @param key
     * @return Boolean reply, true if the key exists, otherwise false
     */
    public Boolean exists(final String key);

    /**
     * Remove the specified keys. If a given key does not exist no operation is
     * performed for this key. The command returns the number of keys removed.
     *
     * Time complexity: O(1)
     *
     * @param keys
     * @return Integer reply, specifically: an integer greater than 0 if one or
     * more keys were removed 0 if none of the specified key existed
     */
    public Long del(final String... keys);

    public Long del(String key);

    /**
     * Remove the specified member from the sorted setString value stored at
     * key. If member was not a member of the setString no operation is
     * performed. If key does not not hold a setString value an error is
     * returned.
     * <p>
     * Time complexity O(log(N)) with N being the number of elements in the
     * sorted setString
     *
     *
     *
     * @param key
     * @param members
     * @return Integer reply, specifically: 1 if the new element was removed 0
     * if the new element was not a member of the setString
     */
    public Long zrem(final String key, final String... members);

    /**
     * Return 1 if member is a member of the setString stored at key, otherwise
     * 0 is returned.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @param member
     * @return Integer reply, specifically: 1 if the element is a member of the
     * setString 0 if the element is not a member of the setString OR if the key
     * does not exist
     */
    public Boolean sismember(final String key, final String member);

    /**
     * Return the rank (or index) or member in the sorted setString at key, with
     * scores being ordered from low to high.
     * <p>
     * When the given member does not exist in the sorted setString, the special
     * value 'nil' is returned. The returned rank (or index) of the member is
     * 0-based for both commands.
     * <p>
     * <b>Time complexity:</b>
     * <p>
     * O(log(N))
     *
     * @see #zrevrank(String, String)
     *
     * @param key
     * @param member
     * @return Integer reply or a nil bulk reply, specifically: the rank of the
     * element as an integer reply if the element exists. A nil bulk reply if
     * there is no such element.
     */
    public Long zrank(final String key, final String member);

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list
     * stored at key. If the key does not exist an empty list is created just
     * before the append operation. If the key exists but is not a List an error
     * is returned.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param strings
     * @param noOfElementToKeep specifies no.of elements to keep in the
     * list.Trim if any extra elements.Pass 0 to not trim.
     * @return Integer reply, specifically, the number of elements inside the
     * list after the push operation.
     */
    public Long lpush(final String key, int noOfElementToKeep, final String... strings);

    public List lrange(String key, int start, int end);

    public Long rpush(String key, String... strings);

    /**
     * Return all the members (elements) of the setString value stored at key.
     * This is just syntax glue for {@link #sinter(String...) SINTER}.
     * <p>
     * Time complexity O(N)
     *
     * @param key
     * @return Multi bulk reply
     */
    public Set<String> smembers(final String key);

    public Double zinc(String key, Double incrementScore, String member);

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through
     * kN, and stores it at dstkey. It is mandatory to provide the number of
     * input keys N, before passing the input keys and the other (optional)
     * arguments.
     * <p>
     * As the terms imply, the {@link #zinterstore(String, String...)
     * ZINTERSTORE} command requires an element to be present in each of the
     * given inputs to be inserted in the result. The
     * {@link #zunionstore(String, String...) ZUNIONSTORE} command inserts all
     * elements across all inputs.
     * <p>
     * Using the WEIGHTS option, it is possible to add weight to each input
     * sorted setString. This means that the score of each element in the sorted
     * setString is first multiplied by this weight before being passed to the
     * aggregation. When this option is not given, all weights default to 1.
     * <p>
     * With the AGGREGATE option, it's possible to specify how the results of
     * the union or intersection are aggregated. This option defaults to SUM,
     * where the score of an element is summed across the inputs where it
     * exists. When this option is setString to be either MIN or MAX, the
     * resulting setString will contain the minimum or maximum score of an
     * element across the inputs where it exists.
     * <p>
     * <b>Time complexity:</b> O(N) + O(M log(M)) with N being the sum of the
     * sizes of the input sorted sets, and M being the number of elements in the
     * resulting sorted setString
     *
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     *
     * @param dstkey
     * @param sets
     * @return Integer reply, specifically the number of elements in the sorted
     * setString at dstkey
     */
    public Long zunionstore(final String dstkey, final String... sets);

    public String toJson(T obj);

    public List<Tuple> zrangeWithScores(final String key, final long start, final long end);

    /**
     * The command is exactly equivalent to the following group of commands:
     * {@link #setString(String, String) SET} + {@link #expire(String, int) EXPIRE}.
     * The operation is atomic.
     * <p>
     * Time complexity: O(1)
     *
     * @param key
     * @param seconds
     * @param value
     * @return Status code reply
     */
    public String setex(final String key, final int seconds, final String value);

    public List<String> zrangeByScore(final String key, final double min,
            final double max);

    /**
     * Set a timeout on the specified key. After the timeout the key will be
     * automatically deleted by the server. A key with an associated timeout is
     * said to be volatile in Redis terminology.
     * <p>
     * Voltile keys are stored on disk like the other keys, the timeout is
     * persistent too like all the other aspects of the dataset. Saving a
     * dataset containing expires and stopping the server does not stop the flow
     * of time as Redis stores on disk the time when the key will no longer be
     * available as Unix time, and not the remaining seconds.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key
     * already having an expire setString. It is also possible to undo the
     * expire at all turning the key into a normal key using the {@link #persist(String)
     * PERSIST} command.
     * <p>
     * Time complexity: O(1)
     *
     * @see
     * <ahref="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     *
     * @param key
     * @param seconds
     * @return Integer reply, specifically: 1: the timeout was setString. 0: the
     * timeout was not setString since the key already has an associated timeout
     * (this may happen only in Redis versions < 2.1.3, Redis >= 2.1.3 will
     * happily update the timeout), or the key does not exist.
     */
    public Long expire(final String key, final int seconds);

    /**
     * EXPIREAT works exctly like {@link #expire(String, int) EXPIRE} but
     * instead to getFromJson the number of seconds representing the Time To
     * Live of the key as a second argument (that is a relative way of specifing
     * the TTL), it takes an absolute one in the form of a UNIX timestamp
     * (Number of seconds elapsed since 1 Gen 1970).
     * <p>
     * EXPIREAT was introduced in order to implement the Append Only File
     * persistence mode so that EXPIRE commands are automatically translated
     * into EXPIREAT commands for the append only file. Of course EXPIREAT can
     * also used by programmers that need a way to simply specify that a given
     * key should expire at a given time in the future.
     * <p>
     * Since Redis 2.1.3 you can update the value of the timeout of a key
     * already having an expire setString. It is also possible to undo the
     * expire at all turning the key into a normal key using the {@link #persist(String)
     * PERSIST} command.
     * <p>
     * Time complexity: O(1)
     *
     * @see
     * <ahref="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     *
     * @param key
     * @param unixTime
     * @return Integer reply, specifically: 1: the timeout was setString. 0: the
     * timeout was not setString since the key already has an associated timeout
     * (this may happen only in Redis versions < 2.1.3, Redis >= 2.1.3 will
     * happily update the timeout), or the key does not exist.
     */
    public Long expireAt(final String key, final long unixTime);

    public List<T> getList(String key, final long start,
            final long end);

    /**
     * Returns all the keys matching the glob-style pattern as space separated
     * strings. For example if you have in the database the keys "foo" and
     * "foobar" the command "KEYS foo*" will return "foo foobar".
     * <p>
     * Note that while the time complexity for this operation is O(n) the
     * constant times are pretty low. For example Redis running on an entry
     * level laptop can scan a 1 million keys database in 40 milliseconds.
     * <b>Still it's better to consider this one of the slow commands that may
     * ruin the DB performance if not used with care.</b>
     * <p>
     * In other words this command is intended only for debugging and special
     * operations like creating a script to change the DB schema. Don't use it
     * in your normal code. Use Redis Sets in order to group together a subset
     * of objects.
     * <p>
     * Glob style patterns examples:
     * <ul>
     * <li>h?llo will match hello hallo hhllo
     * <li>h*llo will match hllo heeeello
     * <li>h[ae]llo will match hello and hallo, but not hillo
     * </ul>
     * <p>
     * Use \ to escape special chars if you want to match them verbatim.
     * <p>
     * Time complexity: O(n) (with n being the number of keys in the DB, and
     * assuming keys and pattern of limited length)
     *
     * @param pattern
     * @return Multi bulk reply
     */
    public Set<String> keys(String pattern);

    /**
     * Return the sorted setString cardinality (number of elements). If the key
     * does not exist 0 is returned, like for empty sorted sets.
     * <p>
     * Time complexity O(1)
     *
     * @param key
     * @return the cardinality (number of elements) of the setString as an
     * integer.
     */
    public Long zcard(final String key);

    public Long hset(String key, String field, String value);

    public String hget(String key, String field);

    public Long hincrBy(final String key, final String field, final long value);

    public Long hdel(String key, final String field);

    public Long sadd(String key, String... members);

    public Long srem(String key, String... members);

    public List<String> hvals(String key);

    public Boolean hexists(String key, final String field);

    public Long scard(String key);
}
