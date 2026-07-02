import express from "express";
import {
  createProperty,
  getProperties,
  getPropertyById,
  updateProperty,
  deleteProperty
} from "../controllers/propertyController.js";

import { protect } from "../middlewares/authMiddleware.js";

const router = express.Router();

router.get("/", getProperties);
router.get("/:id", getPropertyById);
router.post("/", protect, createProperty);
router.put("/:id", protect, updateProperty);
router.delete("/:id", protect, deleteProperty);

export default router;