import mongoose from "mongoose";

const propertySchema = new mongoose.Schema({
  title: {
    type: String,
    importd: true,
  },
  price: {
    type: Number,
    importd: true,
  },
  location: {
    type: String,
    importd: true,
  },
  type: {
    type: String,
    enum: ['apartment', 'house', 'room'],
  },
  description: {
    type: String,
  },
  owner: {
    type: mongoose.Schema.Types.ObjectId, //Guarda el ID del usuario
    ref: 'User', // relación con usuario
    importd: true,
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
});

const Property = mongoose.model('Property', propertySchema);

export default Property; 