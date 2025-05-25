import mongoose from 'mongoose';
import dotenv from 'dotenv';

dotenv.config();

const dbURI = process.env.ATLAS_URI;

if (!dbURI) {
  throw new Error('ATLAS_URI environment variable is not defined');
}

const connectDB = async () => {
  try {
    await mongoose.connect(dbURI);
    console.log('MongoDB Connected...');
  } catch (err) {
    console.error(err.message);
    process.exit(1);
  }
};

export default connectDB;
