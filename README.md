# Stock Manager CLI

**Java CLI for real-time stock tracking with 2-3 trees**

## Project Summary

A lightweight command‑line tool that lets you add, remove, and update stocks and their timestamped price histories. Internally, it uses two 2‑3 tree structures for efficient lookup by **stock ID** and by **current price**.

## Features

* Create and remove stocks with initial price events
* Record price updates (up/down) with timestamps
* Retrieve the current price of any stock
* Remove individual historical price events
* Perform range queries by price: count and list stock IDs

## Project Structure

```text
.
├── Main.java             # CLI interface with menu options
├── StockManager.java     # Manages stocks via two 2-3 trees
├── Stock.java            # Stock model with history and current price
├── TreeOperations.java   # Interface defining 2-3 tree operations
├── Tree.java             # 2-3 tree implementation (with sentinels)
├── Node.java             # Node implementation for 2-3 tree
└── Keys.java             # Composite key class for tree ordering
```

## Usage Examples

**Add a stock**

```text
> 1
Stock ID: TSLA
Timestamp: 1620000000
Price: 600.00
Stock added.
```

**Query current price**

```text
> 4
Stock ID: TSLA
Stock price: 600.00
```

## Assignment Specification

**Data Structures & Algorithms - 00940224 (Technion)**
Implement an in-memory system for stock tracking with the following requirements:

* **Stock model**: unique ID + timestamped price events (initial + updates)
* **Operations**: O(log N) insert/remove/query, O(log N + log M) update/remove history, O(log N) range count, O(log N + K) range list


## Author

Suheil Khourieh
