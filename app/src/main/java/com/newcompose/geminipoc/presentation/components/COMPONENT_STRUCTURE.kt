/*
 * ProductCard Component Structure & Visual Layout
 *
 * This document describes the visual hierarchy and structure of the ProductCard
 */

/**
 * CARD VISUAL STRUCTURE:
 *
 * ┌─────────────────────────────────────────────────┐
 * │  ProductCard (Material3 Surface with Elevation) │
 * │                                                  │
 * │  ┌──────────────────────────────────────────┐  │
 * │  │ Header Section                           │  │
 * │  │ ┌────────────────────┐  ┌──────────────┐ │  │
 * │  │ │ Product Name      │  │ ID: Badge    │ │  │
 * │  │ │ (Bold, Large)     │  │              │ │  │
 * │  │ └────────────────────┘  └──────────────┘ │  │
 * │  └──────────────────────────────────────────┘  │
 * │                                                  │
 * │  ─────────────────────────────────────────────  │ (Divider)
 * │                                                  │
 * │  ┌──────────────────────────────────────────┐  │
 * │  │ Category Row                             │  │
 * │  │ [Tag Icon] Category: Electronics        │  │
 * │  └──────────────────────────────────────────┘  │
 * │                                                  │
 * │  ┌──────────────────────────────────────────┐  │
 * │  │ Price Row                                │  │
 * │  │ [$Icon] Price: $99.99                   │  │
 * │  └──────────────────────────────────────────┘  │
 * │                                                  │
 * │  ┌──────────────────────────────────────────┐  │
 * │  │ Stock Section (Expandable)               │  │
 * │  │ [Inventory Icon] Stock                   │  │
 * │  │                                           │  │
 * │  │ ┌──────────────────────────────────────┐ │  │
 * │  │ │ Stock Details Box (Surfaced)        │ │  │
 * │  │ │                                      │ │  │
 * │  │ │ New York          [15] ✓ (Green)   │ │  │
 * │  │ │ Los Angeles       [8]  ✓ (Green)   │ │  │
 * │  │ │ Chicago           [0]  ✗ (Red)     │ │  │
 * │  │ └──────────────────────────────────────┘ │  │
 * │  └──────────────────────────────────────────┘  │
 * │                                                  │
 * │  ┌──────────────────────────────────────────┐  │
 * │  │ Status Footer (Rounded Box)              │  │
 * │  │ [LocalOffer Icon] In Stock               │  │
 * │  └──────────────────────────────────────────┘  │
 * │                                                  │
 * └─────────────────────────────────────────────────┘
 */

/**
 * COMPONENT HIERARCHY:
 *
 * MainScreen
 * └── Column(vertical list)
 *     ├── OutlinedTextField (search query input)
 *     ├── Button (Ask Gemini)
 *     ├── CircularProgressIndicator (when loading)
 *     └── ProductListView
 *         └── LazyColumn (scrollable, optimized)
 *             └── ProductCard (for each product)
 *                 ├── Header Section
 *                 │   ├── Product Name (Text)
 *                 │   └── ID Badge (Surface)
 *                 ├── Divider (HorizontalDivider)
 *                 ├── Category Row (ProductDetailRow)
 *                 ├── Price Row (ProductDetailRow)
 *                 ├── Stock Section (ProductDetailRow + Details Box)
 *                 │   ├── Stock Header
 *                 │   └── Stock Details
 *                 │       └── StockItem (for each location)
 *                 └── Status Footer (Row + Surface)
 */

/**
 * COLOR SCHEME (Material3):
 *
 * Surface Colors:
 * - Card Background: MaterialTheme.colorScheme.surface
 * - Surface Variant: MaterialTheme.colorScheme.surfaceVariant
 * - Primary Container: MaterialTheme.colorScheme.primaryContainer
 *
 * Text Colors:
 * - Primary Text: MaterialTheme.colorScheme.onSurface
 * - Secondary Text: MaterialTheme.colorScheme.onSurfaceVariant
 * - Badge Text: MaterialTheme.colorScheme.onPrimaryContainer
 *
 * Status Colors:
 * - In Stock: Color(0xFF4CAF50) - Green
 * - Out of Stock: Color(0xFFB71C1C) - Red
 * - Available Badge: Color(0xFFE8F5E9) - Light Green
 * - Unavailable Badge: Color(0xFFFFEBEE) - Light Red
 */

/**
 * SPACING & DIMENSIONS:
 *
 * Card Padding: 20.dp
 * Card Elevation: 8.dp
 * Card Border Radius: 16.dp
 *
 * Section Spacing: 12.dp
 * Icon Size: 20.dp
 * Font Sizes:
 *   - Product Name: 20.sp
 *   - Detail Labels: 12.sp
 *   - Detail Values: 14.sp
 *
 * Stock Detail Box Padding: 12.dp
 * Stock Item Vertical Spacing: 4.dp
 * Stock Badge Padding: 8.dp (horizontal), 4.dp (vertical)
 */

/**
 * DATA FLOW:
 *
 * User Input
 *     ↓
 * MainViewModel.processQuery()
 *     ↓
 * GeminiService (AI processing)
 *     ↓
 * JSONObject (parse response)
 *     ↓
 * ShoppingUseCases.searchProducts()
 *     ↓
 * MainViewModel updates state with displayedProducts
 *     ↓
 * MainScreen observes state and displays ProductListView
 *     ↓
 * ProductListView renders each Product as ProductCard
 */

/**
 * RESPONSIVENESS:
 *
 * - Card uses Modifier.fillMaxWidth() - scales to screen width
 * - LazyColumn uses Modifier.fillMaxSize() - uses all available space
 * - Stock details are wrapped with proper constraints
 * - Text with maxLines = 2 and TextOverflow.Ellipsis for long names
 * - Works on all screen sizes (phone, tablet, landscape)
 */

