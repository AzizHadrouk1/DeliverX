const http = require('http');

const deliveryId = "1";
const route = [
  { lat: 36.8065, lng: 10.1815, note: "Driver left the Tunis main depot", status: "IN_TRANSIT" },
  { lat: 36.8150, lng: 10.1950, note: "Passing through Bab Saadoun", status: "IN_TRANSIT" },
  { lat: 36.8280, lng: 10.2100, note: "En route on the expressway", status: "IN_TRANSIT" },
  { lat: 36.8420, lng: 10.2350, note: "Approaching Charguia sector", status: "IN_TRANSIT" },
  { lat: 36.8590, lng: 10.2600, note: "Arrived at sorting facility for local dispatch", status: "AT_SORTING_FACILITY" },
  { lat: 36.8720, lng: 10.2850, note: "Dispatched from sorting facility", status: "OUT_FOR_DELIVERY" },
  { lat: 36.8850, lng: 10.3100, note: "Entering La Marsa town limits", status: "OUT_FOR_DELIVERY" },
  { lat: 36.8950, lng: 10.3250, note: "Driving through the residential avenue", status: "OUT_FOR_DELIVERY" },
  { lat: 36.8985, lng: 10.3305, note: "Arrived at destination address. Package handed over.", status: "DELIVERED" }
];

let index = 0;

function sendPing() {
  if (index >= route.length) {
    console.log("\n=========================================");
    console.log("Simulation finished successfully!");
    console.log("=========================================");
    process.exit(0);
  }

  const point = route[index];
  const payload = JSON.stringify({
    deliveryId: deliveryId,
    latitude: point.lat,
    longitude: point.lng,
    speed: point.status === "DELIVERED" ? 0 : 50,
    heading: 45,
    notes: point.note
  });

  const postData = (path, method, body, callback) => {
    const req = http.request({
      hostname: 'localhost',
      port: 8090,
      path: path,
      method: method,
      headers: {
        'Content-Type': 'application/json',
        'Content-Length': Buffer.byteLength(body)
      }
    }, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => callback(null, data));
    });

    req.on('error', (e) => callback(e));
    req.write(body);
    req.end();
  };

  const updateStatusAndLocation = () => {
    const statusPayload = JSON.stringify({ status: point.status, notes: point.note });
    
    // First update the status
    postData(`/tracking/api/tracking/${deliveryId}/status`, 'PATCH', statusPayload, (err) => {
      if (err) console.error("Error updating status:", err.message);
      
      // Then post the location ping
      postData(`/tracking/api/tracking/${deliveryId}/location`, 'POST', payload, (err) => {
        if (err) {
          console.error(`[Step ${index + 1}/${route.length}] Error sending coordinates:`, err.message);
        } else {
          console.log(`[Step ${index + 1}/${route.length}] GPS Coordinates: ${point.lat}, ${point.lng} | Status: ${point.status} | Note: "${point.note}"`);
        }
        index++;
        setTimeout(sendPing, 3000); // Delay of 3 seconds between updates
      });
    });
  };

  updateStatusAndLocation();
}

console.log("=========================================");
console.log(`Starting Driver Simulator for Delivery #${deliveryId}`);
console.log("Updating live map positions every 3s...");
console.log("=========================================");
sendPing();
