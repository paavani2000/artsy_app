// const mongoose = require('mongoose');
// const bcrypt = require('bcryptjs');

// const userSchema = new mongoose.Schema({
//   name: { type: String, required: true },
//   email: { type: String, required: true, unique: true },
//   password: { type: String, required: true },
// });

// userSchema.pre('save', async function(next) {
//   if (this.isModified('password')) {
//     this.password = await bcrypt.hash(this.password, 10);
//   }
//   next();
// });

// const User = mongoose.model('User', userSchema);

// module.exports = User;

const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');
const crypto = require('crypto'); // Import crypto module

const userSchema = new mongoose.Schema({
  name: { type: String, required: true },
  email: { type: String, required: true, unique: true },
  password: { type: String, required: true },
  profileImageUrl: { type: String }, // Add profileImageUrl field
  favourites: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Artist' }]
});

userSchema.pre('save', async function (next) {
  if (this.isModified('password')) {
    this.password = await bcrypt.hash(this.password, 10);
  }

  if (this.isNew) { // Generate gravatar url only when creating new user.
    const emailHash = crypto
      .createHash('sha256')
      .update(this.email.trim().toLowerCase())
      .digest('hex');

    this.profileImageUrl = `https://www.gravatar.com/avatar/${emailHash}?d=identicon`;
  }
  next();
});

const User = mongoose.model('User', userSchema);

module.exports = User;
