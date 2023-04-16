package gt.trading.huobi;

import gt.trading.huobi.models.OrderBookData;
import gt.trading.huobi.models.PriceLevel;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * A visualizer for the order book of a cryptocurrency exchange, which displays the top 10 bids and asks in a graphical
 * format.
 */
public class OrderBookVisualizer extends JFrame {

    private JPanel bidsPanel;
    private JPanel asksPanel;

    private List<PriceLevel> askData = null;
    private List<PriceLevel> bidData = null;

    /**
     * Constructs a new instance of the OrderBookVisualizer.
     */
    public OrderBookVisualizer() {
        // Initialize the frame
        super("Orderbook Visualizer");
        setSize(300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the bids panel
        bidsPanel = new JPanel();
        bidsPanel.setLayout(new BoxLayout(bidsPanel, BoxLayout.Y_AXIS));
        bidsPanel.setBorder(BorderFactory.createTitledBorder("Bids"));

        JScrollPane bidsScrollPane = new JScrollPane(bidsPanel);
        bidsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Create the asks panel
        asksPanel = new JPanel();
        asksPanel.setLayout(new BoxLayout(asksPanel, BoxLayout.Y_AXIS));
        asksPanel.setBorder(BorderFactory.createTitledBorder("Asks"));

        JScrollPane asksScrollPane = new JScrollPane(asksPanel);
        asksScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add the panels to the frame
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(asksScrollPane);
        getContentPane().add(bidsScrollPane);
    }

    /**
     * Creates a panel that represents an order.
     *
     * @param totalAmount the total amount of the order
     * @param amount the amount of the order at the given price level
     * @param price the price of the order
     * @param isBid a boolean indicating whether the order is a bid (true) or an ask (false)
     * @return a new JPanel representing the order
     */
    private JPanel createOrderPanel(double totalAmount, double amount, double price, boolean isBid) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel priceLabel = new JLabel(String.format("%.2f", price));
        priceLabel.setHorizontalAlignment(JLabel.LEFT);
        panel.add(priceLabel, BorderLayout.WEST);

        JPanel box = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int barWidth = (int) Math.max(1, 100 * Math.log10(totalAmount));
                int boxHeight = 20;
                int x = 0;
                int y = 0;
                int width = barWidth;
                int height = boxHeight;

                g.setColor(isBid ? new Color(0, 153, 51) : new Color(204, 0, 0));
                g.fillRect(x, y, width, height);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
            }
        };
        box.setPreferredSize(new Dimension(150, 20));

        JLabel sizeLabel = new JLabel(String.format("%.4f (%.2f)", amount, totalAmount));
        sizeLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(sizeLabel, BorderLayout.EAST);

        panel.add(box, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Updates the order book with the given order.
     *
     * @param order The order to update the order book with.
     */
    public void updateOrderBook(OrderBookData newData) {

        if (newData.getAsks().size() >= 10) {
            askData = newData.getAsks().subList(0, 10);
        } else {
            if (askData != null) {
                askData.addAll(newData.getAsks());
                if (askData.size() > 10) {
                    askData = askData.subList(0, 10);
                }
            } else {
                askData = newData.getAsks();
            }
        }

        if (newData.getBids().size() >= 10) {
            bidData = newData.getBids().subList(0, 10);
        } else {
            if (bidData != null) {
                bidData.addAll(newData.getBids());
                if (bidData.size() > 10) {
                    bidData = bidData.subList(0, 10);
                }
            } else {
                bidData = newData.getBids();
            }
        }

        List<PriceLevel> askLevels = askData;
        List<PriceLevel> bidLevels = bidData;

        // Sort the ask levels by price in ascending order
        askLevels.sort(Comparator.comparing(PriceLevel::getPrice));

        // Get the top 10 ask levels
        askLevels = askLevels.subList(0, Math.min(10, askLevels.size()));

        // Sort the bid levels by price in descending order
        bidLevels.sort(Comparator.comparing(PriceLevel::getPrice));
        // Get the top 10 bid levels
        bidLevels = bidLevels.subList(0, Math.min(10, bidLevels.size()));

        // Clear the existing panels
        asksPanel.removeAll();
        bidsPanel.removeAll();

        // Add the ask panels
        double cumulativeAskAmount = 0;
        for (PriceLevel ask : askLevels) {
            double price = ask.getPrice();
            double amount = ask.getAmount();
            cumulativeAskAmount += amount;
            JPanel askPanel = createOrderPanel(cumulativeAskAmount, amount, price, false);
            asksPanel.add(askPanel, 0);
        }


        // Add the bid panels
        double cumulativeBidAmount = 0;
        for (int i = bidLevels.size() - 1; i >= 0; i--) {
            PriceLevel bid = bidLevels.get(i);
            double price = bid.getPrice();
            double amount = bid.getAmount();
            cumulativeBidAmount += amount;
            JPanel bidPanel = createOrderPanel(cumulativeBidAmount, amount, price, true);
            bidsPanel.add(bidPanel);
        }


        bidData.removeIf(level -> level.getAmount() < 0.0001);
        askData.removeIf(level -> level.getAmount() < 0.0001);
    }
}