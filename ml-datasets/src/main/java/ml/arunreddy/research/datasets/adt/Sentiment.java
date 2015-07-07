package ml.arunreddy.research.datasets.adt;

/**
 * Sentiment Abstract Data Type.
 */
public class Sentiment
{

    /**
     * CONSTANTS.
     */
    public static final int TRAIN_INSTANCE = 101;

    public static final int TEST_INSTANCE = 102;

    public static final int VALIDATION_INSTANCE = 103;

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * REF VARS.
     */
    private long id;

    private String sentimentId;

    private String id2;

    private String text;

    private String user;

    private String label;

    /**
     * Represents the type of sentiment instance in context of machine learning classification. The values for different
     * types are as follows:
     * <ul>
     * <li>Training (@see TRAIN_INSTANCE)</li>
     * <li>Test (@see TEST_INSTANCE)</li>
     * <li>Validation (@see VALIDATION_INSTANCE)</li>
     * </ul>
     */
    private int instanceType;

    // Default constructor for Ohm DB instantiation.
    public Sentiment()
    {
        this(null, null, null, null);
    }

    public Sentiment(String sentimentId, String text)
    {
        this(sentimentId, null, text, null);
    }

    public Sentiment(String sentimentId, String text, String user)
    {
        this(sentimentId, null, text, user);
    }

    public Sentiment(String sentimentId, String id2, String text, String user)
    {
        this.sentimentId = sentimentId;
        this.id2 = id2;
        this.text = text;
        this.user = user;
    }

    public String getSentimentId()
    {
        return sentimentId;
    }

    public void setSentimentId(String sentimentId)
    {
        this.sentimentId = sentimentId;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getId2()
    {
        return id2;
    }

    public void setId2(String id2)
    {
        this.id2 = id2;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public int getInstanceType()
    {
        return instanceType;
    }

    public void setInstanceType(int instanceType)
    {
        this.instanceType = instanceType;
    }

    @Override
    public String toString()
    {
        return "Sentiment{" + "sentimentId='" + sentimentId + '\'' + ", text='" + text + '\'' + ", user='" + user + '\''
            + ", label='" + label + '\'' + ", instanceType=" + instanceType + '}';
    }
}
