import { useEffect, useState } from "react";
import { getProperties } from "../api/properties";
import { useLocation } from "react-router-dom";

const Properties = () => {
  const [properties, setProperties] = useState([]);
  const locationHook = useLocation();

  useEffect(() => {
    const fetchData = async () => {
      const params = new URLSearchParams(locationHook.search);
      const filters = {
        location: params.get("location"),
        type: params.get("type"),
        price: params.get("price"),
      };
      let minPrice, maxPrice;

      if (filters.price) {
        const [min, max] = filters.price.split("-");
        minPrice = min;
        maxPrice = max;
      }
      const data = await getProperties({
      location: filters.location,
      type: filters.type,
      minPrice,
      maxPrice,
    });

      setProperties(data);
    };
    fetchData();
  }, [locationHook.search]);
  return (
    <div>
      {properties.map((prop) => (
        <div key={prop._id}>
          <h3>{prop.title}</h3>
          <p>{prop.location}</p>
          <p>${prop.price}</p>
        </div>
      ))}
    </div>
  );
};

export default Properties;
