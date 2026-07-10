# DeliverX — Tracking Microservice (Service de Suivi)

This microservice provides real-time package tracking, live GPS map rendering, routing, and route optimization for the DeliverX ecosystem.

---

## 🛠️ Advanced Technology Stack & Implementation

The tracking feature is built on top of high-performance backend patterns and reactive frontend paradigms:

### 1. Backend (Spring Boot & MongoDB)
*   **MongoDB NoSQL Database:** Used for storing geospatial events (`TrackingEvent`) and optimized routes (`DeliveryRoute`). MongoDB offers fast writes and scale-out capabilities ideal for continuous GPS logs.
*   **STOMP over WebSocket:** Integrates Spring WebSockets with STOMP protocol. When a driver sends location coordinates, the server processes the event and instantly broadcasts it to all connected frontend clients.
*   **ETA Engine (Haversine Formula):** Calculates the distance between the driver's current coordinates and the destination, estimating the arrival time based on average speed.
*   **Route Optimization (Nearest-Neighbor Algorithm):** An advanced algorithm that reorders delivery waypoints to minimize the total travel distance, increasing overall delivery efficiency.
*   **Spring Cloud Ecosystem:** 
    *   **Spring Cloud Gateway:** Central entry point routing HTTP requests (`/tracking/api/**` stripped to `/api/**`) and WebSocket handshakes (`/ws/**`).
    *   **Netflix Eureka Discovery:** Enables the gateway to load-balance traffic to the tracking service dynamically.
    *   **Centralized Config Server (Native Profile):** Enables local configurations to be instantly retrieved and hot-reloaded.

### 2. Frontend (Angular 19 & Leaflet)
*   **Leaflet Maps API:** Embedded interactive map rendering that displays the driver's location, route path, and delivery destination.
*   **Reactive WebSockets (RxJS):** Subscribes to the WebSocket server through the gateway, receiving live location updates to animate the vehicle marker in real-time.
*   **Angular Signals:** Leveraged for state management, reactive computations (like computing timeline progress index), and real-time UI/ETA updates.
*   **Route Parameter Synchronization:** Deep-linking `/track/:id` route parameters with Angular Router so that refreshing the page maintains the active tracking state.

---

## 🚀 Accomplishments & Work Done

1.  **Resolved Compile-Time Type Conflict:** Renamed the duplicate `PackageStatus` in the package model to `PackageMgmtStatus`, resolving a TS2308 type collision in the shared library.
2.  **Enabled Native Config Profile:** Configured the `config-server` to run in native file-system mode rather than fetching from remote GitHub repository, enabling instant local property overrides.
3.  **Corrected Database URI:** Changed the tracking service database URI host to `localhost` so the service can run outside Docker while connecting to the local database.
4.  **Deep-Linked Client Tracking:** Updated the frontend Client Portal to automatically synchronize tracking state with the browser URL (`/track/:id`) so refreshes and browser history work seamlessly.
5.  **Fixed WebSocket Map Initialization:** Patched the map layout logic so that if the page starts with no prior coordinates, receiving the first live GPS ping automatically triggers map initialization and rendering.
