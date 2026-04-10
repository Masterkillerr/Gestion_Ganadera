// Seed file for initial data
// Run with: npx prisma db seed

async function main() {
  console.log('Seeding database...');
  
  // Add seed data here
  
  console.log('Database seeded successfully!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
