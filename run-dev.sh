#!/bin/bash

# RoomRent Development Startup Script
# This script loads environment variables from .env and starts the application

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}RoomRent Development Startup${NC}"
echo ""

# Check if .env exists
if [ ! -f .env ]; then
    echo -e "${RED}ERROR: .env file not found${NC}"
    echo "Please copy .env.example to .env and fill in your credentials:"
    echo "  cp .env.example .env"
    echo "  # Edit .env with your Gmail App Password"
    exit 1
fi

echo -e "${GREEN}✓ Loading environment from .env${NC}"

# Load .env file
set -a
source .env
set +a

# Validate required variables
for var in MAIL_HOST MAIL_PORT MAIL_USERNAME MAIL_PASSWORD MAIL_FROM; do
    if [ -z "${!var}" ]; then
        echo -e "${RED}ERROR: ${var} is not set in .env${NC}"
        exit 1
    fi
done

echo -e "${GREEN}✓ All required variables loaded${NC}"
echo ""
echo "Starting RoomRent with:"
echo "  MAIL_HOST: ${MAIL_HOST}"
echo "  MAIL_PORT: ${MAIL_PORT}"
echo "  MAIL_USERNAME: ${MAIL_USERNAME}"
echo "  MAIL_FROM: ${MAIL_FROM}"
echo ""

# Start the application
java -jar target/room-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  --logging.level.org.springframework.mail=DEBUG \
  --logging.level.com.roomrent.app.service=DEBUG \
  "$@"
