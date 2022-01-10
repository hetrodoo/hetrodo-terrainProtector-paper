# Terrain Protector
The terrain Protector is a paper plugin that protects a user defined area, no one besides the area owner and its members will be allowed to do anything there. 
#### Tested on a spigot-1.16.R3 server.

## Usage
- Craft a **White Banner**
- Place the banner on the ground.
- Using ingots of iron right-click the banner.
- Your claim size will be calculated using the amount of iron that you use. (_Size = ironIngotAmount * 2_)

### Adding/Removing a member to the claim

- Craft a paper
- Rename the paper using an anvil to the member name
- Right-click the banner with the paper
- **_To remove, do the same process with the paper again_**

## Known Issues
- If there is only one area, and it gets disbanded, its registry will not be deleted, making it load again after a server restart.

## Pending
- Optimization to the ClaimManager class to better handle a large amount of claims.

## Contributing
PRs are welcome, if you see something that could be better we can discuss it.

## License
[MIT](https://choosealicense.com/licenses/mit/)