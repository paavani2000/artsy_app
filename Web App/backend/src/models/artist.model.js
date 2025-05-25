const mongoose = require('mongoose');

const artistSchema = new mongoose.Schema({
    name: { type: String, required: true },
    nationality: { type: String },
    years: { type: String }, // Assuming you want to store "birth year - death year" as a string
    artistId: { type: String, required: true }, // Add artistId from external API
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    imageUrl: { type: String, required: true }, // New field
    created_at: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Artist', artistSchema);